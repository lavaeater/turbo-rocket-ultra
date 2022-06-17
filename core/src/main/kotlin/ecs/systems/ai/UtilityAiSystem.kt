package ecs.systems.ai

import ai.deltaTime
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.utils.Pool
import ecs.components.ai.Path
import ecs.components.ai.Waypoint
import ecs.components.gameplay.TransformComponent
import factories.engine
import factories.world
import input.canISeeYouFromHere
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.log.debug
import ktx.math.vec2
import physics.*
import kotlin.reflect.KClass

class UtilityAiSystem(priority: Int) : IteratingSystem(allOf(UtilityAiComponent::class).get()) {
    private val utilMapper = mapperFor<UtilityAiComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val ai = utilMapper.get(entity)
        ai.topAction(entity)?.act(entity, deltaTime)
    }

    /*
    Infinite Axis Utility System

    range: 0-1

    clamp, then normalize

    Action has list of considerations.

    Multiple considerations (axes) with each other,  get 0..1 value

    Makes a sorted list - take the top one.

    Why the FUCK would you multiply normalized values together,
    when you could just simply use an average? Why not an average?
    WHYYY?

    A consideration should simply return a value between zero and one
     */
}

abstract class Consideration {
    abstract fun normalizedScore(entity: Entity): Float
}

class MyHealthConsideration : Consideration() {
    override fun normalizedScore(entity: Entity): Float {
        val attackables = entity.attackables()
        return attackables.health / attackables.maxHealth
    }
}

class CanISeeThisConsideration<ToLookFor : Component>(
    private val componentClass: KClass<ToLookFor>,
    private val stop: Boolean = true
) : Consideration() {
    private val entitiesToLookForFamily = allOf(componentClass, TransformComponent::class).get()
    private val engine by lazy { engine() }
    override fun normalizedScore(entity: Entity): Float {
        val agentProps = entity.agentProps()
        if (stop) {
            agentProps.speed = 0f
        }
        val agentPosition = entity.transform().position
        /*
        Choose a random viewDirection within fov of current viewingdirection! Or some other technique
         */

        val inrangeEntities = engine.getEntitiesFor(entitiesToLookForFamily)
            .filter { it.transform().position.dst(agentPosition) < agentProps.viewDistance }
            .filter {
                canISeeYouFromHere(
                    agentPosition,
                    agentProps.directionVector,
                    it.transform().position,
                    agentProps.fieldOfView
                )
            }
        debug { "LookForAndStore found ${inrangeEntities.size} entities in range and in the field of view" }
        var haveIseenSomething = false
        for (potential in inrangeEntities) {
            val entityPosition = potential.transform().position
            var lowestFraction = 1f
            var closestFixture: Fixture? = null
            val pointOfHit = vec2()
            val hitNormal = vec2()


            world().rayCast(
                agentPosition,
                entityPosition
            ) { fixture, point, normal, fraction ->
                if (fraction < lowestFraction) {
                    lowestFraction = fraction
                    closestFixture = fixture
                    pointOfHit.set(point)
                    hitNormal.set(normal)
                }
                RayCast.CONTINUE
            }

            if (closestFixture != null && closestFixture!!.isEntity() && inrangeEntities.contains(closestFixture!!.getEntity())) {
                debug { "LookForAndStore - entity at $entityPosition can be seen " }
                haveIseenSomething = true
                break
            }
        }
        return if (haveIseenSomething) 1.0f else 0f
    }
}

sealed class AiActionState {
    object Fresh: AiActionState()
    object Running: AiActionState()
    object Paused: AiActionState()
    object Completed: AiActionState()
}

abstract class AiAction {
    val considerations = mutableListOf<Consideration>()

    /**
     * Action State is just used as a convenience for the task
     * itself to see if it should reset itself or something
     * like that.
     *
     * Like, a tasks score could depend on wether or not it is completed
     *
     */
    var actionState: AiActionState = AiActionState.Fresh
    private set

    /***
     * This is open so we can simply implement a static score for something
     * like the Amble task - a task that will be performed unless some other task
     * gets a higher average score - very likely since most of my considerations would be
     * simply yes or no values
     */
    open fun score(entity: Entity): Double {
        return considerations.map { it.normalizedScore(entity) }.average()
    }

    abstract fun act(entity: Entity, deltaTime: Float)
    fun pause() {
        actionState = AiActionState.Paused
    }
}

abstract class StaticScoreAction(private val score: Float): AiAction() {
    override fun score(entity: Entity): Double {
        return score.toDouble()
    }
}

/**
 * We could use a free-floating function to move our little entities
 * towards a target. Very easily done.
 */


/**
 * This action has a static score so unless something scores
 * higher, this will always be executed. We might use Path components
 * or keep
 */
class AmbleAiAction(score: Float):StaticScoreAction(score) {
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

        } else if (!entity.has<Waypoint>()) {


        } else {


        }
    }
}

class UtilityAiComponent : Component, Pool.Poolable {
    val actions = mutableListOf<AiAction>()
    private var currentAction: AiAction? = null
    fun topAction(entity: Entity): AiAction? {
        val potentialAction = actions.minByOrNull { it.score(entity) }
        if(currentAction != potentialAction) {
            currentAction?.pause()
            currentAction = potentialAction
        }
        return currentAction
    }

    override fun reset() {
        actions.clear()
    }

}
