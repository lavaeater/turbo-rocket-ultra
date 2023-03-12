package eater.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import eater.ecs.ashley.components.AiComponent
import eater.ecs.ashley.components.Remove
import ktx.ashley.allOf
import ktx.ashley.exclude

class UpdateActionsSystem : IntervalIteratingSystem(allOf(AiComponent::class).exclude(Remove::class).get(), 0.01f) {

    override fun processEntity(entity: Entity) {
        val ai = AiComponent.get(entity)
        ai.updateAction(entity)
    }
}
