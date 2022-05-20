package ai.builders

import ai.tasks.EntityTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ktx.log.debug
import kotlin.reflect.KClass

class ComponentExistenceGuard<T : Component>(private val mustHave: Boolean, private val componentClass: KClass<T>) :
    EntityTask() {
    @delegate: Transient
    private val mapper by lazy { ComponentMapper.getFor(componentClass.java) }
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        return if (mustHave) {
            if (mapper.has(entity)) {
                debug { "Entity has ${componentClass.simpleName} and this is good" }
                Status.SUCCEEDED
            } else {
                debug { "Entity does not have ${componentClass.simpleName} and this is bad" }
                Status.FAILED
            }
        } else {
            if (mapper.has(entity)) {
                debug { "Entity has ${componentClass.simpleName} and this is bad" }
                Status.FAILED
            } else {
                debug { "Entity does not have ${componentClass.simpleName} and this is good" }
                Status.SUCCEEDED
            }
        }
    }

    override fun toString(): String {
        return if (mustHave)
            "Entity has ${componentClass.simpleName}"
        else
            "Entity hasn't got ${componentClass.simpleName}"
    }
}