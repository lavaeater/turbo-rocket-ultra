package ai.tasks

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class RemoveComponentTask<T: Component>(private val componentClass: Class<T>) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        entity.remove(componentClass)
        return Status.SUCCEEDED
    }

    override fun toString(): String {
        return "RemoveComponent"
    }
}