package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.deltaTime
import ai.format
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class DelayTask(val delayFor: Float) : EntityTask() {
    var delayLeft = delayFor
    override fun resetTask() {
        super.resetTask()
        delayLeft = delayFor
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return DelayTask(delayFor)
    }

    override fun execute(): Status {
        delayLeft -= deltaTime()
        return when {
            delayLeft > 0f -> Status.RUNNING
            else -> Status.SUCCEEDED
        }
    }

    override fun start() {
        super.start()
        delayLeft = delayFor
    }

    override fun toString(): String {
        return "${delayLeft.format(1)} s"
    }
}