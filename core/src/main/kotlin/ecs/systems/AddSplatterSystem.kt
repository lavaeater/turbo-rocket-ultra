package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class AddSplatterSystem: IteratingSystem(allOf(ParticleComponent::class, TransformComponent::class).get()) {
    private val particleMapper = mapperFor<ParticleComponent>()
    private val transformMapper = mapperFor<TransformComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = transformMapper.get(entity)
        val particleComponent = particleMapper.get(entity)
        val bloodEntity = engine.createEntity().apply {
            add(TransformComponent(transformComponent.position.cpy(), transformComponent.rotation))
            add(SplatterComponent(20f, particleComponent.color, 0.2f))
            add(RenderableComponent(0))
        }
        engine.addEntity(bloodEntity)

        particleComponent.life -= deltaTime
        if(particleComponent.life < 0f)
            entity.add(DestroyComponent())
    }
}

