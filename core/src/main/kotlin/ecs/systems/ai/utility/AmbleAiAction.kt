package ecs.systems.ai.utility

import ai.findPathFromTo
import ai.pathfinding.TileGraph
import ai.tasks.leaf.SectionFindingMethods
import com.badlogic.ashley.core.Entity
import ecs.components.ai.Path
import ecs.components.ai.Waypoint
import ecs.systems.graphics.GameConstants
import ecs.systems.sectionX
import ecs.systems.sectionY
import ktx.ashley.mapperFor
import ktx.ashley.remove
import ktx.log.debug
import ktx.math.minus
import physics.*

/**
 * This action has a static score so unless something scores
 * higher, this will always be executed. We might use Path components
 * or keep
 *
 * All AiActions MUST be stateless and contain NO data. They will be kept as
 * objects or something somewhere.
 */
class AmbleAiAction(score: Float = 0.6f) : StaticScoreAction("Amble", score) {

    private val method = SectionFindingMethods::classicRandom
    private fun findAmblePath(entity: Entity) {
        val position = entity.transform().position
        val currentSection = TileGraph.getCoordinateInstance(position.sectionX(), position.sectionY())
        val foundSection = method(currentSection, 3, 5) //must it be able to fail? - yes
        if (foundSection != null) {
            entity.addComponent<Path> {
                val from = currentSection //Remove starting section
                val to = foundSection!! //Keep the other one, might need it, might not
                findPathFromTo(this.queue, from, to)
            }
        }
    }

    override fun abort(entity: Entity) {
        /**
         * Abort means different things to different tasks. Abort in
         * the context of amble means "remove path and waypoints"
         */
        entity.remove<Path>()
        entity.remove<Waypoint>()
    }

    override fun act(entity: Entity, deltaTime: Float) {
        /*
        Ambling comes in stages.
        First we check if we have a Path - if not, we create one
         */

        /**
         * We reuse some of the logic from the behavior tree
         * So, we check if we have a path, if not, we create one
         * and return. The method needs to run fast fast fast
         * so no long hold-ups, we will return here later.
         */
        if (!entity.has<Path>()) {
            findAmblePath(entity)
        } else if (!entity.has<Waypoint>()) {
            findNextWayPoint(entity)
        } else {
            ambleTowardsWayPoint(entity)
        }
    }

    private fun ambleTowardsWayPoint(entity: Entity) {
        val positionToMoveTowards = mapperFor<Waypoint>().get(entity).position
        val currentDistance = positionToMoveTowards.dst(entity.transform().position)

        val agentProps = entity.agentProps()
        agentProps.speed = agentProps.baseSpeed

        val currentPosition = entity.transform().position

        val direction = (positionToMoveTowards - currentPosition).nor()

        agentProps.directionVector.set(direction)
        if (currentDistance < GameConstants.TOUCHING_DISTANCE) {
            debug { "MoveTowards reached destination with $currentDistance to spare " }
            entity.remove<Waypoint>()
        } else {
            /**
             * Best way to check if we are stuck is to check
             * the physics body's linear speed, to see if it is much
             * different from the supposed linear speed we should be going.
             */
            if (entity.body().linearVelocity.len() < agentProps.speed / 2f) {
                val something = "or another"
            }
        }
    }

    private fun findNextWayPoint(entity: Entity) {
        val path = entity.getComponent<Path>()
        if (path.queue.isEmpty) {
            entity.remove<Path>()
            debug { "Queue was empty, try again" }
        }

        val nextStep = path.queue.removeFirst()
        debug { "NextStepOnPath is $nextStep" }
        entity.addComponent<Waypoint> {
            position = nextStep
        }
    }
}