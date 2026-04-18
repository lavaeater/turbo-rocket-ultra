package ai.behaviorTree.builders

import ai.behaviorTree.tasks.EntityComponentTask
import ai.behaviorTree.tasks.HasComponentTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.ai.TaskComponent
import animation.Builder

class EntityComponentTaskBuilder<T: TaskComponent>(private val componentClass: Class<T>) : Builder<Task<Entity>> {
    lateinit var guardingTask: Task<Entity>
    inline fun <reified E: Component>unlessEntityHas() {
        guardingTask = invertResultOf(HasComponentTask(E::class.java))
    }
    inline fun <reified E: Component>ifEntityHas() {
        guardingTask = HasComponentTask(E::class.java)
    }

    override fun build(): Task<Entity> = EntityComponentTask(componentClass).apply { if(::guardingTask.isInitialized) guard = guardingTask }
}