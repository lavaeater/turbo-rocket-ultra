package ai.behaviorTree.tasks.leaf.boss

import ai.behaviorTree.tasks.EntityTask
import ai.deltaTime
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import components.TransformComponent
import components.enemy.AttackableProperties
import components.player.PlayerComponent
import ktx.ashley.allOf
import ktx.math.vec2
import physics.agentProps
import physics.transform
import systems.graphics.GameConstants

/**
 * Boss locks onto the player's position at the moment the rush starts and charges that point.
 * The player can dodge by moving after the rush begins.
 * If the player is still at the target point when the boss arrives, it deals damage.
 */
class RushPlayer(private val damage: Float = 20f) : EntityTask() {
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class, AttackableProperties::class).get()
    private val rushTarget = vec2()
    private var previousDistance = Float.MAX_VALUE
    private var stuckCooldown = 0f

    override fun start() {
        super.start()
        val player = engine.getEntitiesFor(playerFamily).firstOrNull()
        if (player != null) {
            rushTarget.set(player.transform().position)
        } else {
            rushTarget.setZero()
        }
        previousDistance = Float.MAX_VALUE
        stuckCooldown = 0.5f
    }

    override fun execute(): Status {
        val agentProps = entity.agentProps()
        val bossPos = entity.transform().position

        if (rushTarget.isZero) return Status.FAILED

        val toTarget = rushTarget.cpy().sub(bossPos)
        val distance = toTarget.len()

        if (distance <= GameConstants.TOUCHING_DISTANCE) {
            agentProps.speed = 0f
            dealDamageIfPlayerNearby(bossPos)
            return Status.SUCCEEDED
        }

        agentProps.directionVector.set(toTarget.nor())
        agentProps.speed = agentProps.rushSpeed

        stuckCooldown -= deltaTime()
        if (stuckCooldown <= 0f) {
            stuckCooldown = 0.5f
            if (previousDistance - distance <= GameConstants.STUCK_DISTANCE) {
                agentProps.speed = 0f
                return Status.FAILED
            }
            previousDistance = distance
        }

        return Status.RUNNING
    }

    private fun dealDamageIfPlayerNearby(bossPos: Vector2) {
        engine.getEntitiesFor(playerFamily)
            .filter { it.transform().position.dst(bossPos) <= GameConstants.TOUCHING_DISTANCE * 2f }
            .forEach { AttackableProperties.get(it).takeDamage(damage, entity) }
    }

    override fun resetTask() {
        super.resetTask()
        rushTarget.setZero()
        previousDistance = Float.MAX_VALUE
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = RushPlayer(damage)

    override fun toString() = "Rush Player"
}
