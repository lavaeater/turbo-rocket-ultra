package ai.tasks.leaf

import ai.tasks.EntityTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
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

/**
 * Checks to see if this particular entity can see
 * ANY entity with the provided component type T
 *
 *
 *
 */
class CanSeeAnyThatHas<T : Component>(val coolDown: Float = 0.2f) : EntityTask() {
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
                if(debug) {
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