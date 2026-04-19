package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.deltaTime
import ai.format
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import physics.addComponent
import components.ai.PositionTarget
import components.ai.StuckComponent
import systems.graphics.GameConstants
import ktx.log.debug
import physics.*
import kotlin.reflect.KClass

class MoveTowardsPositionTarget<T: PositionTarget>(val run: Boolean = false, val componentClass: KClass<T>) : EntityTask() {
    var previousDistance = 0f
    var currentDistance = 0f
    var positionToMoveTowards = Vector2.Zero.cpy()
    var coolDown = 0.5f
    var actualCoolDown = coolDown
    var needsPosition = true
    val mapper by lazy { ComponentMapper.getFor(componentClass.java) }

    private var sliding = false
    private val slideDir = Vector2.Zero.cpy()

    override fun start() {
        super.start()
        actualCoolDown = coolDown
        positionToMoveTowards = Vector2.Zero.cpy()
        needsPosition = true
        sliding = false
        slideDir.setZero()
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return MoveTowardsPositionTarget(run, componentClass)
    }

    override fun execute(): Status {
        if (!mapper.has(entity))
            return Status.FAILED

        if (needsPosition) {
            positionToMoveTowards = mapper.get(entity).position
            previousDistance = positionToMoveTowards.dst(entity.transform().position)
            currentDistance = previousDistance
            needsPosition = false
        }

        val agentProps = entity.agentProps()
        agentProps.speed = if (run) agentProps.rushSpeed else agentProps.baseProperties.speed

        val currentPosition = entity.transform().position
        currentDistance = positionToMoveTowards.dst(currentPosition)

        if (currentDistance < GameConstants.TOUCHING_DISTANCE) {
            debug { "MoveTowards reached destination" }
            entity.remove(componentClass.java)
            sliding = false
            return Status.SUCCEEDED
        }

        if (sliding) {
            agentProps.directionVector.set(slideDir)
        } else {
            val direction = positionToMoveTowards.cpy().sub(currentPosition).nor()
            agentProps.directionVector.set(direction)
        }

        actualCoolDown -= deltaTime()
        if (actualCoolDown < 0f) {
            actualCoolDown = coolDown
            val newDistance = positionToMoveTowards.dst(entity.transform().position)
            val progress = previousDistance - newDistance

            if (progress <= GameConstants.STUCK_DISTANCE) {
                if (sliding) {
                    // Still stuck while sliding — give up
                    debug { "MoveTowards stuck while sliding, failing" }
                    entity.addComponent<StuckComponent>()
                    entity.remove(componentClass.java)
                    sliding = false
                    return Status.FAILED
                }
                // First stuck — pick the perpendicular that closes distance to target
                val blockedDir = positionToMoveTowards.cpy().sub(currentPosition).nor()
                val perp1 = Vector2(-blockedDir.y, blockedDir.x)
                val perp2 = Vector2(blockedDir.y, -blockedDir.x)
                val d1 = positionToMoveTowards.dst(currentPosition.cpy().add(perp1))
                val d2 = positionToMoveTowards.dst(currentPosition.cpy().add(perp2))
                slideDir.set(if (d1 < d2) perp1 else perp2)
                sliding = true
                debug { "MoveTowards stuck, sliding perpendicular" }
            } else {
                if (sliding && progress > GameConstants.STUCK_DISTANCE * 10f) {
                    // Made meaningful progress while sliding — resume heading straight to target
                    sliding = false
                    debug { "MoveTowards slide successful, resuming direct approach" }
                }
            }
            previousDistance = newDistance
        }
        return Status.RUNNING
    }

    override fun resetTask() {
        super.resetTask()
        sliding = false
        slideDir.setZero()
    }

    override fun toString(): String {
        val mode = if (sliding) "sliding" else "→"
        return "Move towards ${componentClass.simpleName} $mode ${currentDistance.format(1)} | ${(previousDistance - currentDistance).format(2)}"
    }
}