package ai.builders

import ai.tasks.HasComponentTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent

class HasComponentBuilder<T: Component>(private val componentClass: Class<T>) : Builder<Task<Entity>> {
    override fun build(): Task<Entity> = HasComponentTask(componentClass)
}