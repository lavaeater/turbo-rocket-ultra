package ai.tasks.leaf

import ai.aimTowards
import ai.deltaTime
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.PositionTarget
import ecs.components.ai.StuckComponent
import ecs.systems.graphics.GameConstants
import ktx.ashley.remove
import ktx.math.vec2
import physics.*

class MoveTowardsPositionTarget(private val run: Boolean = false) : EntityTask() {
    var previousDistance = 0f
    var currentDistance = 0f
    var positionToMoveTowards = vec2()
    var coolDown = 1f
    var actualCoolDown = coolDown
    override fun start() {
        super.start()
        actualCoolDown = coolDown
        positionToMoveTowards = entity.getComponent<PositionTarget>().position
        previousDistance = positionToMoveTowards.dst(entity.transform().position)
        currentDistance = previousDistance
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }


    override fun execute(): Status {
        if (!entity.has<PositionTarget>())
            return Status.FAILED
        val agentProps = entity.agentProps()
        agentProps.speed = if (run) agentProps.rushSpeed else agentProps.baseSpeed
        agentProps.directionVector.set(entity.transform().position.aimTowards(positionToMoveTowards))
        actualCoolDown -= deltaTime()
        if (actualCoolDown < 0f) {
            actualCoolDown = coolDown
            currentDistance = positionToMoveTowards.dst(entity.transform().position)
            if(previousDistance - currentDistance <= GameConstants.STUCK_DISTANCE) {
                //Add a "StuckComponent" to the entity.
                entity.addComponent<StuckComponent>()
                entity.remove<PositionTarget>()
                return Status.FAILED
            } else if(currentDistance < GameConstants.TOUCHING_DISTANCE){
                entity.remove<PositionTarget>()
                return Status.SUCCEEDED
            } else {
                previousDistance = currentDistance
            }
        }
        return Status.SUCCEEDED
    }
}