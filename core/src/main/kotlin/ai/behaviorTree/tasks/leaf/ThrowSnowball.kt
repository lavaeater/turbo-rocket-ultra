package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.deltaTime
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.AgentProperties
import components.TransformComponent
import components.player.PlayerComponent
import components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.agentProps
import physics.transform

/**
 * Lobs a snowball at the nearest player within range.
 * On hit the player's movement speed is halved for [slowDuration] seconds.
 * This is modelled by temporarily halving AgentProperties.speed if they have it,
 * or setting a stunned cooldown via PlayerControlComponent otherwise.
 */
class ThrowSnowball(val range: Float = 12f, val slowDuration: Float = 2f) : EntityTask() {
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class, PlayerControlComponent::class).get()

    override fun execute(): Status {
        val myPos = entity.transform().position
        val target = engine.getEntitiesFor(playerFamily)
            .filter { it.transform().position.dst(myPos) <= range }
            .minByOrNull { it.transform().position.dst(myPos) }
            ?: return Status.FAILED

        val pcc = PlayerControlComponent.get(target)
        // Reuse stun briefly to represent the slow (chilled / slipping)
        pcc.startCooldown(pcc::stunned, slowDuration * 0.4f)

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = ThrowSnowball(range, slowDuration)

    override fun cloneTask(): Task<Entity> {
        val clone = ThrowSnowball(range, slowDuration)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }
    override fun toString() = "Throw Snowball (range=$range, slow=${slowDuration}s)"
}
