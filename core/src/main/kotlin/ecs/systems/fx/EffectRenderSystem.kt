package ecs.systems.fx

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.fx.CreateEntityComponent
import ecs.components.fx.ParticleEffectComponent
import ecs.components.gameplay.DestroyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.graphics.use
import physics.addComponent
import physics.getComponent

class DelayedEntityCreationSystem(): IteratingSystem(allOf(CreateEntityComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val creator = entity.getComponent<CreateEntityComponent>()
        creator.creator()
        engine.removeEntity(entity)
    }

}

class EffectRenderSystem(private val batch: Batch) : IteratingSystem(
    allOf(
        ParticleEffectComponent::class,
        TransformComponent::class
    ).get(), 2) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val effectComponent = entity.getComponent<ParticleEffectComponent>()
        if(effectComponent.ready) {
            val transform = entity.getComponent<TransformComponent>()
            val effect = effectComponent.effect
            if(effect.isComplete) {
                entity.addComponent<DestroyComponent>()
            }
            for(emitter in effect.emitters) {
                emitter.setPosition(transform.position.x, transform.position.y)
                if(!effectComponent.started) {
                    emitter.start()
                }
            }
            if(!effectComponent.started) {
                effectComponent.started = true
            }
            effect.update(deltaTime)
            batch.use {
                effect.draw(batch)
            }
        }
    }

}