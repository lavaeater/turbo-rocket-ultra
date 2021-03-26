package ai.builders

import ai.tasks.AddComponentTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent

class AddComponentBuilder<T : TaskComponent>(private val componentClass: Class<T>) : Builder<Task<Entity>> {
    override fun build(): Task<Entity> = AddComponentTask(componentClass)
}