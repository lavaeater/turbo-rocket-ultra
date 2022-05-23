package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.AgentProperties
import ecs.components.enemy.AttackableProperties
import ecs.components.fx.ParticleEffectComponent
import ecs.components.gameplay.BurningComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.getComponent

class BurningSystem: IteratingSystem(allOf(BurningComponent::class, AttackableProperties::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attackableProperties = entity.getComponent<AttackableProperties>()
        val burningComponent = entity.getComponent<BurningComponent>()

        burningComponent.coolDown -= deltaTime
        if(burningComponent.coolDown <= 0f) {
            entity.remove<BurningComponent>()
            entity.remove<ParticleEffectComponent>()
        } else {
            attackableProperties.takeDamage(burningComponent.damageRange.random() * deltaTime, burningComponent.player)
        }
    }
}

