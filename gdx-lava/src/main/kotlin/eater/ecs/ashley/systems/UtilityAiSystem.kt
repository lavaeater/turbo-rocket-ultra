package eater.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.AiComponent
import eater.ecs.ashley.components.Remove
import ktx.ashley.allOf
import ktx.ashley.exclude

class UtilityAiSystem : IteratingSystem(allOf(AiComponent::class).exclude(Remove::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val ai = AiComponent.get(entity)
        if(ai.topAction(entity)?.act(entity, deltaTime) == true) {
            ai.updateAction(entity)
        }
    }
}

