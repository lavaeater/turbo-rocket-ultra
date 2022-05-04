package ai.tasks

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class HasComponentTask<T: Component>() : EntityTask() {
    lateinit var componentClass: Class<T>
    constructor(componentClass: Class<T>): this() {
        this.componentClass = componentClass
    }
    @delegate: Transient
    private val mapper by lazy { ComponentMapper.getFor(componentClass) }
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        return if(mapper.has(entity)) Status.SUCCEEDED else Status.FAILED
    }
    override fun toString(): String {
        return "EntityHasComponent?"
    }
}