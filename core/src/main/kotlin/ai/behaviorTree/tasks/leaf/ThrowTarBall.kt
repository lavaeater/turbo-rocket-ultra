package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.TransformComponent
import components.enemy.AttackableProperties
import components.player.PlayerComponent
import components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.transform

/**
 * Lobs a ball of tar that coats the player, severely impeding movement.
 * Longer duration slow than a snowball; the player is sticky, not stunned.
 * Modelled as a longer partial stun.
 */
class ThrowTarBall(val range: Float = 10f, val slowDuration: Float = 3f) : EntityTask() {
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class, PlayerControlComponent::class).get()

    override fun execute(): Status {
        val myPos = entity.transform().position
        val target = engine.getEntitiesFor(playerFamily)
            .filter { it.transform().position.dst(myPos) <= range }
            .minByOrNull { it.transform().position.dst(myPos) }
            ?: return Status.FAILED

        val pcc = PlayerControlComponent.get(target)
        pcc.startCooldown(pcc::stunned, slowDuration * 0.6f)

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = ThrowTarBall(range, slowDuration)

    override fun cloneTask(): Task<Entity> {
        val clone = ThrowTarBall(range, slowDuration)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }
    override fun toString() = "Throw Tar Ball (range=$range, slow=${slowDuration}s)"
}
