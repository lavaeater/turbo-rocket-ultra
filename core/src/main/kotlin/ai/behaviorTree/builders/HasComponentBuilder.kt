package ai.behaviorTree.builders

import ai.behaviorTree.tasks.HasComponentTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import tru.Builder

class HasComponentBuilder<T: Component>(private val componentClass: Class<T>) : Builder<Task<Entity>> {
    override fun build(): Task<Entity> = HasComponentTask(componentClass)
}