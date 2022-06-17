package ecs.systems.ai.utility

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import kotlin.reflect.KClass

class MoveTowards<ToLookFor : Component>(
    private val componentClass: KClass<ToLookFor>,
    private val stop: Boolean = true
) : AiAction() {
    override fun abort(entity: Entity) {
        TODO("Not yet implemented")
    }

    override fun act(entity: Entity, deltaTime: Float) {
        TODO("Not yet implemented")
    }

}