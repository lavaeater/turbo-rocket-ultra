package ai.tasks.leaf

import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.Path
import ecs.components.ai.PositionTarget
import ktx.ashley.remove
import physics.*


class NextStepOnPath : EntityTask() {
    override fun start() {
        entity.remove<PositionTarget>() //Make sure we do NOT have this component, no matter what
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        if (!entity.has<Path>())
            return Status.FAILED
        val path = entity.getComponent<Path>()
        if (path.queue.isEmpty) {
            //Remove PathComponent!
            entity.remove<Path>()
            return Status.FAILED
        }
        val nextStep = path.queue.removeFirst()
        entity.addComponent<PositionTarget> {
            position = nextStep
        }
        return Status.SUCCEEDED
    }
}