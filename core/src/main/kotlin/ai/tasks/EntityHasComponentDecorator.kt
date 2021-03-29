package ai.tasks

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class EntityHasComponentDecorator<T : Component>(child: Task<Entity>, private val componentClass: Class<T>) :
    EntityDecorator(child) {
    private val mapper by lazy { ComponentMapper.getFor(componentClass) }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun childRunning(runningTask: Task<Entity>?, reporter: Task<Entity>?) {
        if(!mapper.has(entity))
            childFail(runningTask)
        else
            super.childRunning(runningTask, reporter)
    }
}