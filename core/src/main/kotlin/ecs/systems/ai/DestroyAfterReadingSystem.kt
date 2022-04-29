package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.DestroyAfterCoolDownComponent
import ecs.components.gameplay.DestroyComponent
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.addComponent

class DestroyAfterReadingSystem: IteratingSystem(allOf(DestroyAfterCoolDownComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val damageEffectComponent = AshleyMappers.destroyAfterReading.get(entity)
        damageEffectComponent.coolDown -= deltaTime
        if(damageEffectComponent.coolDown <= 0f) {
            entity.addComponent<DestroyComponent>()
        }
    }
}