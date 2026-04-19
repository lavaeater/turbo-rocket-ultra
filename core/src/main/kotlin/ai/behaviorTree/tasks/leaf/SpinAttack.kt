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
 * Enemy spins in place for a full revolution, dealing [damage] to any player within [radius].
 * RUNNING while spinning, SUCCEEDED once the rotation completes.
 */
class SpinAttack(val damage: Float = 15f, val radius: Float = 3f) : EntityTask() {
    private var rotated = 0f
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class, AttackableProperties::class).get()

    override fun start() {
        super.start()
        rotated = 0f
    }

    override fun resetTask() {
        super.resetTask()
        rotated = 0f
    }

    override fun execute(): Status {
        val rotSpeed = entity.agentProps().rotationSpeed
        val toRotate = deltaTime() * rotSpeed
        rotated += toRotate
        entity.agentProps().directionVector.rotateDeg(toRotate)
        entity.agentProps().speed = 0f

        val myPos = entity.transform().position
        engine.getEntitiesFor(playerFamily)
            .filter { it.transform().position.dst(myPos) <= radius }
            .forEach { AttackableProperties.get(it).takeDamage(damage * deltaTime(), entity) }

        return if (rotated >= 360f) Status.SUCCEEDED else Status.RUNNING
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = SpinAttack(damage, radius)

    override fun cloneTask(): Task<Entity> {
        val clone = SpinAttack(damage, radius)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }
    override fun toString() = "Spin Attack (dmg=$damage, r=$radius)"
}
