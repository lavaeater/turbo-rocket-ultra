package ai.tasks

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class AddComponentTask<T: Component>(private val componentClass: Class<T>) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        entity.add(engine.createComponent(componentClass))
        return Status.SUCCEEDED
    }
}