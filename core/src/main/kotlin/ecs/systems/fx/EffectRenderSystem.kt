package ecs.systems.fx

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.fx.ParticleEffectComponent
import eater.ecs.components.TransformComponent
import ktx.ashley.allOf

class EffectRenderSystem(private val batch: Batch, priority:Int) : IteratingSystem(
    allOf(
        ParticleEffectComponent::class,
        TransformComponent::class
    ).get(), priority) {
    override fun processEntity(entity: Entity, deltaTime: Float) {

    }

}