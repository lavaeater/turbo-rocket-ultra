package ai.tasks.leaf

import ai.tasks.EntityTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.Task
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
        val agentProps = entity.agentProps()
        val position = entity.transform().position
        return when (status) {
            Status.FRESH -> {
                startRunning(agentProps)
            }
            Status.RUNNING -> {
                canSee(agentProps, position)
            }
            else -> status
        }
    }

    private fun startRunning(agentProps: AgentProperties): Status {
        if (entity.has<NoticedSomething>()) {
            val noticeVector = entity.getComponent<NoticedSomething>().noticedWhere
            agentProps.directionVector.set(noticeVector).sub(agentProps.directionVector).nor()
        } else {
            //Always check 5 degrees per "turn" until done from where you are
            val unitVectorRange = -1f..1f
            agentProps.directionVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
                .rotateDeg(-agentProps.fieldOfView / 2)
        }
        return Status.RUNNING
    }

    var scanCount = 0
    var foundAPlayer = false
    val scanDirection = vec2()
    var closestFixture: Fixture? = null
    var scanResolution = 1f
    fun maxNumberOfScans(fieldOfView: Float): Float = fieldOfView / scanResolution

    override fun reset() {
        super.reset()
        actualCoolDown = coolDown
        scanCount = 0
        foundAPlayer = false
        scanDirection.setZero()
        closestFixture = null
    }

    fun canSee(agentProps: AgentProperties, position: Vector2): Status {
        actualCoolDown -= deltaTime()
        if (actualCoolDown <= 0f) {
            actualCoolDown = coolDown
            val inRange =
                engine.getEntitiesFor(family).filter { it.transform().position.dst(position) < agentProps.viewDistance }
            var lowestFraction = 1f
            val pointOfHit = vec2()
            val hitNormal = vec2()
            scanCount++
            for (e in inRange) {
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
                            if (fraction < lowestFraction && fixture.isPlayer()) {
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
                            return Status.SUCCEEDED
                        }
                        if (!foundAPlayer) {
                            agentProps.directionVector.rotateDeg(scanResolution)
                            if (scanCount > maxNumberOfScans(agentProps.fieldOfView)) {
                                return Status.FAILED
                            }
                        }
                    }
                }
            }
        }
        return Status.RUNNING
    }
}