package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.ComplexActionComponent
import ecs.components.player.ComplexActionResult
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.complexAction

class ComplexActionSystem: IteratingSystem(allOf(ComplexActionComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val complexAction = entity.complexAction()
        val result = complexAction.doneFunction()
        if(result != ComplexActionResult.Running) {
            complexAction.busy = false
            for(callback in complexAction.doneCallBacks) {
                callback(result)
            }
            if(result == ComplexActionResult.Success) {
                entity.remove<ComplexActionComponent>()
            }
        }
    }
}