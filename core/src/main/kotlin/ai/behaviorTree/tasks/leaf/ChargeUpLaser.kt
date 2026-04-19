package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.deltaTime
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.TransformComponent
import components.enemy.AttackableProperties
import components.player.PlayerComponent
import ktx.ashley.allOf
import physics.agentProps
import physics.transform

/**
 * Boss-tier attack: the enemy locks on to the player's position, charges glowing laser eyes
 * for [chargeTime] seconds (RUNNING, telegraped — move!), then fires a beam that deals
 * [damage] to the player if still in the original aim line (within a cone).
 *
 * Implementation: during charge the enemy freezes. At release, damages player if they
 * haven't moved more than 4 units from the aim point.
 */
class ChargeUpLaser(val chargeTime: Float = 2f, val damage: Float = 50f) : EntityTask() {
    private var elapsed = 0f
    private var aimX = 0f
    private var aimY = 0f
    private val hitConeRadius = 4f
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class, AttackableProperties::class).get()

    override fun start() {
        super.start()
        elapsed = 0f
        val myPos = entity.transform().position
        val target = engine.getEntitiesFor(playerFamily).minByOrNull { it.transform().position.dst(myPos) }
        if (target != null) {
            aimX = target.transform().position.x
            aimY = target.transform().position.y
            // Face the target
            entity.agentProps().directionVector.set(aimX - myPos.x, aimY - myPos.y).nor()
        }
    }

    override fun resetTask() {
        super.resetTask()
        elapsed = 0f
    }

    override fun execute(): Status {
        entity.agentProps().speed = 0f  // frozen while charging
        elapsed += deltaTime()

        if (elapsed < chargeTime) return Status.RUNNING

        // Fire! Hit players still near the aim point
        engine.getEntitiesFor(playerFamily)
            .filter {
                val pos = it.transform().position
                val dx = pos.x - aimX
                val dy = pos.y - aimY
                Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() <= hitConeRadius
            }
            .forEach { AttackableProperties.get(it).takeDamage(damage, entity) }

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = ChargeUpLaser(chargeTime, damage)

    override fun cloneTask(): Task<Entity> {
        val clone = ChargeUpLaser(chargeTime, damage)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }
    override fun toString() = "Charge Laser Eyes (charge=${chargeTime}s, dmg=$damage)"
}
