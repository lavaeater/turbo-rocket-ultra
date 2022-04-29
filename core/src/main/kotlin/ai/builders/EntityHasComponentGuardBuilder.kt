package ai.builders

import ai.tasks.EntityHasComponentDecorator
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class EntityHasComponentGuardBuilder<T : Component>(child: Task<Entity>, val componentClass: Class<T>) : DecoratorBuilder<Entity>(child) {

    override fun build(): Task<Entity> = EntityHasComponentDecorator(child, componentClass)

    override fun add(task: Task<Entity>) {
    }
}