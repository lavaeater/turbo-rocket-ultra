package ai.builders

import ai.tasks.EntityDoesNotHaveComponentDecorator
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task

class EntityDoesNotHaveComponentGuardBuilder<T : Component>(child: Task<Entity>, val componentClass: Class<T>) : DecoratorBuilder<Entity>(child) {
    override fun build(): Task<Entity> = EntityDoesNotHaveComponentDecorator(child, componentClass)

    override fun add(task: Task<Entity>) {

    }
}