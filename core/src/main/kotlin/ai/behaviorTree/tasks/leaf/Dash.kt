package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.deltaTime
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import components.Box2d
import components.TransformComponent
import components.enemy.AttackableProperties
import components.player.PlayerComponent
import ktx.ashley.allOf
import ktx.math.vec2
import physics.agentProps
import physics.transform

/**
 * Brief explosive dash toward the player's current position.
 * Deals [damage] if the player is hit (within contact range at dash end).
 * RUNNING while travelling; SUCCEEDED on arrival or timeout.
 */
class Dash(val speed: Float = 30f, val damage: Float = 10f) : EntityTask() {
    private val dashDuration = 0.3f
    private var elapsed = 0f
    private val dashDir = vec2()
    private val contactRange = 1.5f
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class, AttackableProperties::class, Box2d::class).get()

    override fun start() {
        super.start()
        elapsed = 0f
        val myPos = entity.transform().position
        val target = engine.getEntitiesFor(playerFamily)
            .minByOrNull { it.transform().position.dst(myPos) }
        if (target != null)
            dashDir.set(target.transform().position).sub(myPos).nor()
        else
            dashDir.set(entity.agentProps().directionVector)
    }

    override fun resetTask() {
        super.resetTask()
        elapsed = 0f
    }

    override fun execute(): Status {
        elapsed += deltaTime()
        entity.agentProps().directionVector.set(dashDir)
        entity.agentProps().speed = speed

        if (elapsed >= dashDuration) {
            entity.agentProps().speed = 0f
            val myPos = entity.transform().position
            engine.getEntitiesFor(playerFamily)
                .filter { it.transform().position.dst(myPos) <= contactRange }
                .forEach { AttackableProperties.get(it).takeDamage(damage, entity) }
            return Status.SUCCEEDED
        }
        return Status.RUNNING
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = Dash(speed, damage)

    override fun cloneTask(): Task<Entity> {
        val clone = Dash(speed, damage)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }
    override fun toString() = "Dash (spd=$speed, dmg=$damage)"
}
