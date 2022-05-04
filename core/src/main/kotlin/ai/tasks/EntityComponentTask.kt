package ai.tasks

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.TaskComponent

class EntityComponentTask<T: TaskComponent>() : EntityTask() {
    lateinit var componentClass: Class<T>
    constructor(componentClass: Class<T>) : this() {
        this.componentClass = componentClass
    }
    @delegate: Transient
    private val mapper by lazy { ComponentMapper.getFor(componentClass) }

    override fun start() {
        entity.add(engine.createComponent(componentClass))
        super.start()
    }

    override fun end() {
        entity.remove(componentClass)
        super.end()
    }

    override fun resetTask() {
        entity.remove(componentClass)
        super.resetTask()
    }

    override fun reset() {
        entity.remove(componentClass)
        super.reset()
    }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        return if(!mapper.has(entity)) Status.FAILED else mapper[entity].status
    }

    override fun toString(): String {
        return if(mapper.has(entity)) mapper.get(entity).toString() else ""
    }
}