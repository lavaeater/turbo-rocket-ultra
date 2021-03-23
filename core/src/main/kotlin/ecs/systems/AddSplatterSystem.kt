package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ecs.components.*
import injection.Context
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.graphics.use
import tru.Assets

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

class SplatterRemovalSystem: IteratingSystem(allOf(SplatterComponent::class).get()) {
    val splatterMapper = mapperFor<SplatterComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val splatterComponent = splatterMapper.get(entity)
        splatterComponent.life -= deltaTime
        if(splatterComponent.life < 0f)
            engine.removeEntity(entity)
    }
}