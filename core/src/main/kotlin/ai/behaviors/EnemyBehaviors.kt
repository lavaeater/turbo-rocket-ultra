package ai.behaviors

import ai.findPathFromTo
import ai.pathfinding.TileGraph
import ai.tasks.leaf.SectionFindingMethods
import com.badlogic.ashley.core.Entity
import eater.ai.Consideration
import eater.ai.ConsideredActionWithState
import ecs.components.ai.behavior.AmbleStateComponent
import ecs.systems.graphics.GameConstants.TOUCHING_DISTANCE
import ecs.systems.sectionX
import ecs.systems.sectionY
import ktx.math.minus
import physics.agentProps
import physics.attackables
import physics.transform

object EnemyBehaviors {

    val ambleAction = ConsideredActionWithState(
        "Amble",
        {}, { entity, state, deltaTime ->
            /**
             * What kind of state do we need? Well, we need to check
             * if we are going somewhere, right now, or not.
             *
             * If not, we want to find a place to go to.
             *
             * When we get there, we start over again.
             *
             * Just focus on ambling, right now. Other actions come after
             * this.
             */
            when (state.state) {
                AmbleStateComponent.AmbleState.FindingPathToTarget -> findPathToTarget(entity, state, deltaTime)
                AmbleStateComponent.AmbleState.FindingTargetCoordinate -> findTarget(entity, state, deltaTime)
                AmbleStateComponent.AmbleState.MoveToWaypoint -> move(entity, state)
                AmbleStateComponent.AmbleState.NotStarted -> {
                    entity.agentProps().speed = 0f
                    state.state =
                        AmbleStateComponent.AmbleState.FindingTargetCoordinate
                }
                AmbleStateComponent.AmbleState.NeedsWaypoint -> getWaypoint(entity, state, deltaTime)
            }
        }, AmbleStateComponent::class,
        EnemyConsiderations.healthConsideration
    )

    private fun getWaypoint(entity: Entity, state: AmbleStateComponent, deltaTime: Float) {
        entity.agentProps().speed = 0f
        if(state.wayPoint == null) {
            if (state.ready(deltaTime)) {
                if(state.queue.any()) {
                    state.wayPoint = state.queue.removeFirst()
                    state.state = AmbleStateComponent.AmbleState.MoveToWaypoint
                } else {
                    state.wayPoint = null
                    state.state = AmbleStateComponent.AmbleState.NotStarted
                }
            }
        } else {
            state.state = AmbleStateComponent.AmbleState.MoveToWaypoint
        }
    }

    private fun move(entity: Entity, state: AmbleStateComponent) {
        if(state.wayPoint == null) {
            state.state = AmbleStateComponent.AmbleState.NeedsWaypoint
        } else {
            val currentPos = entity.transform().position
            if(currentPos.dst(state.wayPoint) < TOUCHING_DISTANCE) {
                state.wayPoint = null
                state.state = AmbleStateComponent.AmbleState.NeedsWaypoint // Safeguard
            } else {
                entity.agentProps().directionVector.set(state.wayPoint!! - currentPos).nor()
                entity.agentProps().speed = entity.agentProps().baseProperties.speed

            }
        }
    }

    private fun findPathToTarget(entity: Entity, state: AmbleStateComponent, deltaTime: Float) {
        entity.agentProps().speed = 0f
        if(state.ready(deltaTime)) {
            findPathFromTo(state.queue, state.startPointCoordinate!!, state.endPointCoordinate!!)
            state.state = AmbleStateComponent.AmbleState.MoveToWaypoint
        }
    }

    private fun findTarget(entity: Entity, state: AmbleStateComponent, deltaTime: Float) {
        entity.agentProps().speed = 0f
        if(state.ready(deltaTime)) {
            val position = entity.transform().position
            state.startPointCoordinate = TileGraph.getCoordinateInstance(position.sectionX(), position.sectionY())
            val foundSection = SectionFindingMethods.classicRandom(
                state.startPointCoordinate!!,
                3,
                5
            ) //must it be able to fail? - no, not in this case. if this fail, we randomize
            state.state = AmbleStateComponent.AmbleState.FindingPathToTarget
            if (foundSection == null) {
                state.endPointCoordinate = SectionFindingMethods.randomOfAll(state.startPointCoordinate!!)
            } else {
                state.endPointCoordinate = foundSection
            }
        }
    }
}


object EnemyConsiderations {
    val healthConsideration = Consideration("How's my health?") { entity ->
        val attackables = entity.attackables()
        attackables.health / attackables.maxHealth
    }
}