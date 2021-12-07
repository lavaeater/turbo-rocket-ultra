package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.ComplexActionComponent
import ktx.ashley.allOf
import physics.complexAction

class ComplexActionSystem: IteratingSystem(allOf(ComplexActionComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val complexAction = entity.complexAction()
        if(complexAction.doneFunction()) {
            for(callback in complexAction.doneCallBacks) {
                callback()
            }
        }
    }
}