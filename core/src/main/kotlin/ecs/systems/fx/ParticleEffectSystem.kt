package ecs.systems.fx

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.fx.BloodParticle
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf

class ParticleEffectSystem : IteratingSystem(allOf(TransformComponent::class, BloodParticle::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {

    }
}