package ai.builders

import ai.tasks.RemoveComponentTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent
import tru.Builder

class RemoveComponentBuilder<T : TaskComponent> (private val componentClass: Class<T>): Builder<Task<Entity>> {
    override fun build(): Task<Entity> = RemoveComponentTask(componentClass)
}