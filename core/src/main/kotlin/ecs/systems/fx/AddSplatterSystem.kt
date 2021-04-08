package ecs.systems.fx

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.DestroyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.ParticleComponent
import ecs.components.graphics.RenderableComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.addComponent

class AddSplatterSystem: IteratingSystem(allOf(ParticleComponent::class, TransformComponent::class).get()) {
    private val particleMapper = mapperFor<ParticleComponent>()
    private val transformMapper = mapperFor<TransformComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = transformMapper.get(entity)
        val particleComponent = particleMapper.get(entity)
        val bloodEntity = engine.createEntity().apply {
            addComponent<TransformComponent> {
                position.set(transformComponent.position)
                rotation = transformComponent.rotation
            }
            addComponent<SplatterComponent> {
                color = particleComponent.color
                radius = 0.2f
            }
            addComponent<RenderableComponent>()
        }
        engine.addEntity(bloodEntity)

        particleComponent.life -= deltaTime
        if(particleComponent.life < 0f)
            entity.addComponent<DestroyComponent>()
    }
}
