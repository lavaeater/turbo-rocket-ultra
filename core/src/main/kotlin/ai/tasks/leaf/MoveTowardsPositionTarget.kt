package ai.tasks.leaf

import ai.aimTowards
import ai.deltaTime
import ai.format
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import ecs.components.ai.PositionTarget
import ecs.components.ai.StuckComponent
import ecs.systems.graphics.GameConstants
import ktx.ashley.remove
import ktx.log.debug
import ktx.log.info
import ktx.math.vec2
import physics.*

class MoveTowardsPositionTarget(private val run: Boolean = false) : EntityTask() {
    var previousDistance = 0f
    var currentDistance = 0f
    var positionToMoveTowards = Vector2.Zero.cpy()
    var coolDown = 0.5f
    var actualCoolDown = coolDown
    var needsPosition = true
    override fun start() {
        super.start()
        actualCoolDown = coolDown
        positionToMoveTowards = Vector2.Zero.cpy()
        needsPosition = true
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }


    override fun execute(): Status {
        if (!entity.has<PositionTarget>())
            return Status.FAILED

        if(needsPosition) {
            positionToMoveTowards = entity.getComponent<PositionTarget>().position
            previousDistance = positionToMoveTowards.dst(entity.transform().position)
            currentDistance = previousDistance
            needsPosition = false
        }

        val agentProps = entity.agentProps()
        agentProps.speed = if (run) agentProps.rushSpeed else agentProps.baseSpeed

        val currentPosition = entity.transform().position

        val direction = positionToMoveTowards.cpy().sub(currentPosition).nor()

        agentProps.directionVector.set(direction)
        actualCoolDown -= deltaTime()
        if (actualCoolDown < 0f) {
            actualCoolDown = coolDown
            currentDistance = positionToMoveTowards.dst(entity.transform().position)
            if(previousDistance - currentDistance <= GameConstants.STUCK_DISTANCE) {
                debug { "MoveTowards got stuck" }
                entity.addComponent<StuckComponent>()
                entity.remove<PositionTarget>()
                return Status.FAILED
            } else if(currentDistance < GameConstants.TOUCHING_DISTANCE){
                debug { "MoveTowards reached destination with $currentDistance to spare " }
                entity.remove<PositionTarget>()
                return Status.SUCCEEDED
            }
            previousDistance = currentDistance
        }
        return Status.RUNNING
    }

    override fun toString(): String {
        return "${currentDistance.format(1)} | ${(previousDistance - currentDistance).format(2)}"
    }
}