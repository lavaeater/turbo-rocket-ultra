package ai.tasks.leaf

import ai.tasks.EntityTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.NoticedSomething
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.math.random
import physics.agentProps
import physics.getComponent
import physics.has
import physics.transform
import kotlin.reflect.KClass

/**
 * Checks to see if this particular entity can see
 * ANY entity with the provided component type T
 *
 *
 *
 */
class CanSeeAnyThatHas<T: Component>(val coolDown: Float = 0.1f) : EntityTask() {
    lateinit var componentClass: KClass<T>
    constructor(componentClass: KClass<T>): this() {
        this.componentClass = componentClass
    }
    val family by lazy { allOf(componentClass, TransformComponent::class).get()}

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return CanSeeAnyThatHas<T>()
    }
    var actualCoolDown = coolDown

    override fun execute(): Status {
        val agentProps = entity.agentProps()
        val position = entity.transform().position
        return when(status) {
            Status.FRESH -> {
                actualCoolDown = coolDown
                if (entity.has<NoticedSomething>()) {
                    val noticeVector = entity.getComponent<NoticedSomething>().noticedWhere
                    agentProps.directionVector.set(noticeVector).sub(agentProps.directionVector).nor()
                } else {
                    //Always check 5 degrees per "turn" until done from where you are
                    val unitVectorRange = -1f..1f
                    agentProps.directionVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
                }
                Status.RUNNING
            }
            Status.RUNNING -> {
                actualCoolDown -= GdxAI.getTimepiece().deltaTime
                if(actualCoolDown <= 0f) {
                    val entitiesToCheck = engine.getEntitiesFor(family).filter{ it.transform().position.dst(position) < agentProps.viewDistance }

                }
                Status.RUNNING
            }
            Status.FAILED -> TODO()
            Status.SUCCEEDED -> TODO()
            Status.CANCELLED -> TODO()
        }

    }
}