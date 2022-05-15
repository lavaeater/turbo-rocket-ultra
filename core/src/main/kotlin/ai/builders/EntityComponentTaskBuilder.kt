package ai.builders

import ai.tasks.EntityComponentTask
import ai.tasks.HasComponentTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.old.TaskComponent
import tru.Builder

class EntityComponentTaskBuilder<T: TaskComponent>(private val componentClass: Class<T>) : Builder<Task<Entity>> {
    lateinit var guardingTask: Task<Entity>
    inline fun <reified E: Component>unlessEntityHas() {
        guardingTask = invertResultFrom(HasComponentTask(E::class.java))
    }
    inline fun <reified E: Component>ifEntityHas() {
        guardingTask = HasComponentTask(E::class.java)
    }

    override fun build(): Task<Entity> = EntityComponentTask(componentClass).apply { if(::guardingTask.isInitialized) guard = guardingTask }
}