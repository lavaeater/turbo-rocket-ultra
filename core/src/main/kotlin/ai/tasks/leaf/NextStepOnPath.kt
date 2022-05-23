package ai.tasks.leaf

import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.Path
import ecs.components.ai.PositionTarget
import ecs.components.ai.Waypoint
import ktx.ashley.remove
import ktx.log.debug
import ktx.log.info
import physics.*


class NextStepOnPath : EntityTask() {
    override fun start() {
        entity.remove<Waypoint>() //Make sure we do NOT have this component, no matter what
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        if (!entity.has<Path>()) {
            debug { "Entity did not have Path Component" }
            return Status.FAILED
        }
        val path = entity.getComponent<Path>()
        if (path.queue.isEmpty) {
            entity.remove<Path>()
            debug { "Queue was empty, failing" }
            return Status.FAILED
        }

        val nextStep = path.queue.removeFirst()
        debug { "NextStepOnPath is $nextStep" }
        entity.addComponent<Waypoint> {
            position = nextStep
        }
        return Status.SUCCEEDED
    }

    override fun toString(): String {
        return "Get Next Step on Path"
    }
}