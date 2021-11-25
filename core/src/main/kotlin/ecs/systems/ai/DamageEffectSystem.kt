package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.DestroyAfterCoolDownComponent
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import physics.addComponent
import physics.getComponent

class DestroyAfterReadingSystem(): IteratingSystem(allOf(DestroyAfterCoolDownComponent::class).get()) {
    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val damageEffectComponent = entity.getComponent<DestroyAfterCoolDownComponent>()
        damageEffectComponent.coolDown -= deltaTime
        if(damageEffectComponent.coolDown <= 0f) {
            entity.addComponent<DestroyComponent>()
        }
    }
}