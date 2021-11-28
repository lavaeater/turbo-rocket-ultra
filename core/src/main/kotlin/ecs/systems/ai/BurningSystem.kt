package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.EnemyComponent
import ecs.components.fx.ParticleEffectComponent
import ecs.components.gameplay.BurningComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.getComponent

class BurningSystem: IteratingSystem(allOf(BurningComponent::class, EnemyComponent::class).get()) {
    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = entity.getComponent<EnemyComponent>()
        val burningComponent = entity.getComponent<BurningComponent>()

        burningComponent.coolDown -= deltaTime
        if(burningComponent.coolDown <= 0f) {
            entity.remove<BurningComponent>()
            entity.remove<ParticleEffectComponent>()
        } else {
            enemyComponent.takeDamage(burningComponent.damageRange.random() * deltaTime, burningComponent.player)
        }
    }
}

