package ecs.systems.fx

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.fx.ParticleEffectComponent
import ecs.components.gameplay.DestroyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.graphics.use
import physics.addComponent
import physics.getComponent

class EffectRenderSystem(private val batch: Batch, priority:Int) : IteratingSystem(
    allOf(
        ParticleEffectComponent::class,
        TransformComponent::class
    ).get(), priority) {
    override fun processEntity(entity: Entity, deltaTime: Float) {

    }

}