package ai.builders

import ai.tasks.EntityComponentTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent

class EntityComponentTaskBuilder<T: TaskComponent>(private val componentClass: Class<T>) : Builder<Task<Entity>> {
    override fun build(): Task<Entity> = EntityComponentTask(componentClass)
}