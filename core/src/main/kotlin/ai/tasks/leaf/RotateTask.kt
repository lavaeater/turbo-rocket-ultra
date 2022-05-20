package ai.tasks.leaf

import ai.deltaTime
import ai.format
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import physics.agentProps

class RotateTask(private val degrees: Float, private val counterClockwise: Boolean = true) : EntityTask() {
    private var rotatedSoFar = 0f
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        entity.agentProps().speed = 0f
        val toRotate = deltaTime() * entity.agentProps().rotationSpeed
        rotatedSoFar += toRotate
        return if(rotatedSoFar >= degrees) {
            Status.SUCCEEDED
        }
        else {
            entity.agentProps().directionVector.rotateDeg(if(counterClockwise) toRotate else -toRotate)
            Status.RUNNING
        }

    }

    override fun start() {
        super.start()
        rotatedSoFar = 0f
    }

    override fun resetTask() {
        super.resetTask()
        rotatedSoFar = 0f
    }
    override fun toString(): String {
        return "Rotate $degrees"
    }
}