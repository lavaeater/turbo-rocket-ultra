package ai.tasks.leaf

import ai.aimTowards
import ai.deltaTime
import ai.format
import ai.tasks.EntityTask
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import ecs.components.ai.PositionTarget
import ecs.components.ai.StuckComponent
import ecs.systems.graphics.GameConstants
import ktx.ashley.mapperFor
import ktx.ashley.remove
import ktx.log.debug
import ktx.log.info
import ktx.math.vec2
import physics.*
import kotlin.reflect.KClass

class MoveTowardsPositionTarget<T: PositionTarget>(private val run: Boolean = false, private val componentClass: KClass<T>) : EntityTask() {
    var previousDistance = 0f
    var currentDistance = 0f
    var positionToMoveTowards = Vector2.Zero.cpy()
    var coolDown = 0.5f
    var actualCoolDown = coolDown
    var needsPosition = true
    val mapper by lazy { ComponentMapper.getFor(componentClass.java)  }

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
        if (!mapper.has(entity))
            return Status.FAILED

        if(needsPosition) {
            positionToMoveTowards = mapper.get(entity).position
            previousDistance = positionToMoveTowards.dst(entity.transform().position)
            currentDistance = previousDistance
            needsPosition = false
        }

        val agentProps = entity.agentProps()
        agentProps.speed = if (run) agentProps.rushSpeed else agentProps.baseProperties.speed

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
                entity.remove(componentClass.java)
                return Status.FAILED
            } else if(currentDistance < GameConstants.TOUCHING_DISTANCE){
                debug { "MoveTowards reached destination with $currentDistance to spare " }
                entity.remove(componentClass.java)
                return Status.SUCCEEDED
            }
            previousDistance = currentDistance
        }
        return Status.RUNNING
    }

    override fun toString(): String {
        return "Move towards ${componentClass.simpleName} ${currentDistance.format(1)} | ${(previousDistance - currentDistance).format(2)}"
    }
}