package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.SplatterComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class SplatterRemovalSystem: IteratingSystem(allOf(SplatterComponent::class).get()) {
    private val splatterMapper = mapperFor<SplatterComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val splatterComponent = splatterMapper.get(entity)
        splatterComponent.life -= deltaTime
        if(splatterComponent.life < 0f)
            engine.removeEntity(entity)
    }
}