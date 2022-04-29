package ai.tasks

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class HasComponentTask<T: Component>(private val componentClass: Class<T>) : EntityTask() {
    private val mapper by lazy { ComponentMapper.getFor(componentClass) }
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    @ExperimentalStdlibApi
    override fun execute(): Status {
        return if(mapper.has(entity)) Status.SUCCEEDED else Status.FAILED
    }
    private val classInfo = componentClass.toString()
    override fun toString(): String {
        return classInfo
    }
}