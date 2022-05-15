package ai.tasks.leaf

import ai.deltaTime
import ai.format
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class DelayTask(private val delayFor: Float) : EntityTask() {
    var delayLeft = delayFor
    override fun resetTask() {
        super.resetTask()
        delayLeft = delayFor
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
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