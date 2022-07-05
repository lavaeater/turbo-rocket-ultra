package ai.tasks.leaf

import ai.deltaTime
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.Path
import ecs.components.enemy.AttackableProperties
import eater.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.agentProps
import physics.getComponent
import physics.transform
import kotlin.reflect.KClass

class AttackTarget<T : Component>(private val targetComponentClass: KClass<T>) : EntityTask() {
    private val coolDown = 1f
    private var actualCoolDown = coolDown
    private val attackableFamily =
        allOf(targetComponentClass, TransformComponent::class, AttackableProperties::class).get()

    private fun entitiesInMeleeRange(): List<Entity> {
        return engine.getEntitiesFor(attackableFamily)
            .filter { it.transform().position.dst(entity.transform().position) < entity.agentProps().meleeDistance }
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        if (entitiesInMeleeRange().isEmpty()) return Status.FAILED

        entity.agentProps().speed = 0f
        entity.remove<Path>()
        actualCoolDown -= deltaTime()
        return if (actualCoolDown < 0f) {
            actualCoolDown = coolDown
            val target = entitiesInMeleeRange().random()
            val healthAndStuff = target.getComponent<AttackableProperties>()
            healthAndStuff.takeDamage(10f, entity)
            Status.SUCCEEDED
        } else {
            Status.RUNNING
        }
    }

    override fun resetTask() {
        super.resetTask()
        actualCoolDown = coolDown
    }

    override fun toString(): String {
        return "Attack Target with ${targetComponentClass.simpleName}"
    }

}