package ai.tasks

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent

class CheckComponentStatusTask<T:TaskComponent>(componentClass: Class<T>) : EntityTask() {
    private val mapper by lazy { ComponentMapper.getFor(componentClass)}
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        return mapper.get(entity).status
    }

    override fun toString(): String {
        return mapper.get(entity).toString()
    }

}

