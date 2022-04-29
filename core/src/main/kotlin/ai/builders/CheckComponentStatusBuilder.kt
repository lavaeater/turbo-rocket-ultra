package ai.builders

import ai.tasks.CheckComponentStatusTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent
import tru.Builder

class CheckComponentStatusBuilder<T : TaskComponent>(private val componentClass: Class<T>) : Builder<Task<Entity>> {
    override fun build() : Task<Entity> = CheckComponentStatusTask(componentClass)
}