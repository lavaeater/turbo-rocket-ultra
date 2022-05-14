package ai.tasks.leaf

import ai.tasks.EntityTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.utils.Pool
import ecs.components.ai.IsAwareOfPlayer
import ecs.components.ai.NoticedSomething
import ecs.components.enemy.AgentProperties
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerComponent
import factories.world
import input.canISeeYouFromHere
import ktx.ashley.allOf
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.graphics.use
import ktx.log.info
import ktx.math.random
import ktx.math.vec2
import physics.*
import kotlin.reflect.KClass

fun deltaTime(): Float {
    return GdxAI.getTimepiece().deltaTime
}

class SeenPlayerPositions : PositionStorageComponent()

open class PositionStorageComponent : Component, Pool.Poolable {
    val positions = mutableListOf<Vector2>()
    override fun reset() {
        positions.clear()
    }

}

class RotateTask(private val degrees: Float, private val counterClockwise: Boolean = true) : EntityTask() {
    private var rotatedSoFar = 0f
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        val toRotate = deltaTime() * entity.agentProps().rotationSpeed
        rotatedSoFar += toRotate
        return if(rotatedSoFar >= degrees)
            Status.SUCCEEDED
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
        return "${rotatedSoFar.format(1)} of $degrees"
    }
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)

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

/**
 * Evolve this idea later, of being able to define some kind of storage
 * Component Dynamically
 *
 * Perhaps we have subtypes of
 *
 * Actually, if we just set a list of vectors, that list updates automagically
 * since the vectors are by ref, not val, which is very cool.
 *
 * Let's try that.
 */
class LookForAndStore<ToLookFor : Component, ToStoreIn : PositionStorageComponent>(
    private val componentClass: KClass<ToLookFor>,
    private val storageComponentClass: KClass<ToStoreIn>
) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    private val entitiesToLookForFamily = allOf(componentClass, TransformComponent::class).get()
    private val mapper by lazy { ComponentMapper.getFor(storageComponentClass.java) }
    override fun execute(): Status {
        /* We only, note, ONLY check if we can see entities
        of a certain type.
        We can see them if they are:
        1. Within range
        2. Within sight sector
        3. Not behind an obstacle

        This should add entities to some kind of collection of entities we see, in some kind of component.
        This component can then be used for other behaviors.


        Entities to find MUST have transformComponent!
        */
        val agentProps = entity.agentProps()
        agentProps.speed = 0f
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
        val seenEntityPositions = mutableListOf<Vector2>()
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
                seenEntityPositions.add(entityPosition)
            }
        }
        val storageComponent =
            if (mapper.has(entity)) mapper.get(entity) else engine.createComponent(storageComponentClass.java)
        storageComponent.positions.clear()
        return if (seenEntityPositions.any()) {
            storageComponent.positions.addAll(seenEntityPositions)
            entity.add(storageComponent)
            Status.SUCCEEDED
        } else {
            entity.remove(storageComponentClass.java)
            Status.FAILED
        }
    }

    override fun toString(): String {
        return "Looking for $componentClass"
    }
}

/**
 * Checks to see if this particular entity can see
 * ANY entity with the provided component type T
 * Maybe Can see should simply check WITHOUT the turning stuff?
 *
 * This way we can add some other task, like "turn one degree" or something
 * that will turn the player for some number of times.
 *
 *
 */
class CanSeeAnyThatHas<T : Component>(val coolDown: Float = 0.1f) : EntityTask() {
    lateinit var componentClass: KClass<T>

    constructor(componentClass: KClass<T>) : this() {
        this.componentClass = componentClass
    }

    val family by lazy { allOf(componentClass, TransformComponent::class).get() }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return CanSeeAnyThatHas<T>()
    }

    var actualCoolDown = coolDown

    override fun execute(): Status {
        return canSee(entity.agentProps(), entity.transform().position)
    }

    override fun start() {
        super.start()
        startRunning(entity.agentProps())
    }

    private fun startRunning(agentProps: AgentProperties) {
        previousSpeed = agentProps.speed
        agentProps.speed = 0f

        if (entity.has<NoticedSomething>()) {
            val noticeVector = entity.getComponent<NoticedSomething>().noticedWhere
            agentProps.directionVector.set(noticeVector).sub(agentProps.directionVector).nor()
        } else {
            val unitVectorRange = -1f..1f
            agentProps.directionVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
                .rotateDeg(-agentProps.fieldOfView / 2)
        }
    }

    var scanCount = 0
    var foundAPlayer = false
    val scanDirection = vec2()
    var closestFixture: Fixture? = null
    var scanResolution = 1f
    fun maxNumberOfScans(fieldOfView: Float): Float = fieldOfView / scanResolution

    override fun resetTask() {
        actualCoolDown = coolDown
        scanCount = 0
        foundAPlayer = false
        scanDirection.setZero()
        closestFixture = null
        super.resetTask()
    }

    var previousSpeed = 0f
    val debug = true

    fun canSee(agentProps: AgentProperties, position: Vector2): Status {

        actualCoolDown -= deltaTime()
        if (actualCoolDown <= 0f) {
            info { "Will check for player now" }
            actualCoolDown = coolDown
            val inRange =
                engine.getEntitiesFor(family).filter { it.transform().position.dst(position) < agentProps.viewDistance }
            info { "Players in range: ${inRange.size}" }
            var lowestFraction = 1f
            val pointOfHit = vec2()
            val hitNormal = vec2()
            scanCount++
            for (e in inRange) {
                if (debug) {
                    shapeDrawer.batch.use {
                        shapeDrawer.filledCircle(e.transform().position, 1f, Color.RED)
                    }
                }
                if (!foundAPlayer) {
                    val entityPosition = e.transform().position

                    if (canISeeYouFromHere(
                            position,
                            agentProps.directionVector,
                            entityPosition,
                            agentProps.fieldOfView
                        )
                    ) {

                        scanDirection.set(
                            entityPosition.cpy()
                                .add(entityPosition.cpy().sub(position).nor().scl(agentProps.viewDistance))
                        )

                        world().rayCast(
                            entityPosition,
                            scanDirection
                        ) { fixture, point, normal, fraction ->
                            if (fraction < lowestFraction) {
                                lowestFraction = fraction
                                closestFixture = fixture
                                pointOfHit.set(point)
                                hitNormal.set(normal)
                            }
                            RayCast.CONTINUE
                        }

                        if (closestFixture != null && closestFixture!!.isPlayer()) {
                            foundAPlayer = true

                            entity.add(
                                engine.createComponent(IsAwareOfPlayer::class.java)
                                    .apply { this.player = e.getComponent<PlayerComponent>().player })
                            agentProps.speed = previousSpeed
                            return Status.SUCCEEDED
                        }

                    }
                }
            }
            if (!foundAPlayer) {
                agentProps.directionVector.rotateDeg(scanResolution)
                if (scanCount > maxNumberOfScans(agentProps.fieldOfView)) {
                    agentProps.speed = previousSpeed
                    return Status.FAILED
                }
            }
        }

        return Status.RUNNING
    }
}