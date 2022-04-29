package ai.tasks

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Decorator
import com.badlogic.gdx.ai.btree.Task
import injection.Context

abstract class EntityDecorator(child: Task<Entity>) : Decorator<Entity>(child) {
    protected val engine: Engine by lazy { Context.inject() }
    protected val entity: Entity get() = `object`
}