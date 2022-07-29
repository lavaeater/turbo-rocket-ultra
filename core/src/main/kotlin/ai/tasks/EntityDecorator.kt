package ai.tasks

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Decorator
import com.badlogic.gdx.ai.btree.Task
import eater.injection.InjectionContext.Companion.inject
import injection.Context

fun EntityDecorator.invertDecorator() : EntityDecorator {
    val rr = when(this) {
        is EntityDoesNotHaveComponentDecorator<*> -> EntityHasComponentDecorator(this.getChild(0), this.componentClass)
        is EntityHasComponentDecorator<*> -> EntityDoesNotHaveComponentDecorator(this.getChild(0), this.componentClass)
        else -> this
    }
    return rr
}

abstract class EntityDecorator(child: Task<Entity>) : Decorator<Entity>(child) {
    protected val engine: Engine by lazy { inject() }
    protected val entity: Entity get() = `object`
}