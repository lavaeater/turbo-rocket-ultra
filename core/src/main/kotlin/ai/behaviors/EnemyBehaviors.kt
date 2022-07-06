package ai.behaviors

import ai.pathfinding.TileGraph
import ai.tasks.leaf.SectionFindingMethods
import com.badlogic.ashley.core.Entity
import eater.ai.Consideration
import eater.ai.ConsideredActionWithState
import ecs.components.ai.behavior.AmbleStateComponent
import ecs.systems.sectionX
import ecs.systems.sectionY
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
                AmbleStateComponent.AmbleState.FindingPathToTarget -> TODO()
                AmbleStateComponent.AmbleState.FindingTargetCoordinate -> findTarget(entity, state, deltaTime)
                AmbleStateComponent.AmbleState.MoveToNextStepOnPath -> TODO()
                AmbleStateComponent.AmbleState.NotStarted -> {
                    /*
                    Also, stop moving. While looking, we do absolutely nothing, mate.
                    we need a cool way to handle cooldowns for stuff, to make interrupts happen etc.

                     */
                    entity.agentProps().directionVector.setZero()
                    state.state =
                        AmbleStateComponent.AmbleState.FindingTargetCoordinate
                }
            }
        }, AmbleStateComponent::class
    )

    private fun findTarget(entity: Entity, state: AmbleStateComponent, deltaTime: Float) {
        val position = entity.transform().position
        val currentSection = TileGraph.getCoordinateInstance(position.sectionX(), position.sectionY())
        val foundSection = SectionFindingMethods.classicRandom(currentSection, 3, 5) //must it be able to fail? - no, not in this case. if this fail, we randomize
        state.state = AmbleStateComponent.AmbleState.FindingPathToTarget
        if (foundSection == null) {
            state.endpointCoordinate = SectionFindingMethods.randomOfAll(currentSection)
        } else {
            state.endpointCoordinate = foundSection
        }
    }

}


object EnemyConsiderations {
    val healthConsideration = Consideration("How's my health?") { entity ->
        val attackables = entity.attackables()
        attackables.health / attackables.maxHealth
    }
}