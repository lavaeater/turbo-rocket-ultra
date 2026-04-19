package ai.behaviorTree.tasks.leaf.boss

import ai.behaviorTree.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.TransformComponent
import components.player.PlayerComponent
import ktx.ashley.allOf
import physics.agentProps
import physics.transform

class PlayerIsInGrabRange(val grabRange: Float) : EntityTask() {
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class).get()

    override fun execute(): Status {
        val player = engine.getEntitiesFor(playerFamily).firstOrNull() ?: return Status.FAILED
        val distance = entity.transform().position.dst(player.transform().position)
        return if (distance <= grabRange) Status.SUCCEEDED else Status.FAILED
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = PlayerIsInGrabRange(grabRange)

    override fun toString() = "Player in grab range ($grabRange)"
}
