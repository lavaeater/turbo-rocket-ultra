package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.TransformComponent
import components.enemy.AttackableProperties
import components.player.PlayerComponent
import ktx.ashley.allOf
import physics.transform

/**
 * Hurls a glass bottle at the nearest player within [range].
 * On hit it shatters, dealing AoE [damage] to anyone within the splash radius.
 * Succeeds immediately — no charge time.
 */
class ThrowBottle(val range: Float = 15f, val damage: Float = 25f) : EntityTask() {
    private val splashRadius = 3f
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class, AttackableProperties::class).get()

    override fun execute(): Status {
        val myPos = entity.transform().position
        val target = engine.getEntitiesFor(playerFamily)
            .filter { it.transform().position.dst(myPos) <= range }
            .minByOrNull { it.transform().position.dst(myPos) }
            ?: return Status.FAILED

        val hitPos = target.transform().position
        // Splash: damage everyone in radius around the hit point
        engine.getEntitiesFor(playerFamily).filter { it.transform().position.dst(hitPos) <= splashRadius }
            .forEach { AttackableProperties.get(it).takeDamage(damage, entity) }

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = ThrowBottle(range, damage)

    override fun cloneTask(): Task<Entity> {
        val clone = ThrowBottle(range, damage)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }
    override fun toString() = "Throw Bottle (range=$range, dmg=$damage)"
}
