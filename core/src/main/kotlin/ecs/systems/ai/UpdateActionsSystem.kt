package ecs.systems.ai

import ai.utility.UtilityAiComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf

class UpdateActionsSystem : IntervalIteratingSystem(allOf(UtilityAiComponent::class).get(), 1f) {
    override fun processEntity(entity: Entity) {
        val ai = UtilityAiComponent.get(entity)
        ai.updateAction(entity)
    }

}