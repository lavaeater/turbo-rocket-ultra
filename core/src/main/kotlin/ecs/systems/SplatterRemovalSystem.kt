package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.SplatterComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class SplatterRemovalSystem: IteratingSystem(allOf(SplatterComponent::class).get()) {
    private val splatterMapper = mapperFor<SplatterComponent>()
    private var timeBeforeCleanUp = 3f
    private var timeToCleanUp = false


    override fun update(deltaTime: Float) {
        timeBeforeCleanUp -= deltaTime
        if(timeBeforeCleanUp < 0f)
            timeToCleanUp = true

        super.update(deltaTime)
        if(timeToCleanUp) {
            timeToCleanUp = false
            timeBeforeCleanUp = 3f
        }
    }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val splatterComponent = splatterMapper.get(entity)
        splatterComponent.life -= deltaTime
        if(timeToCleanUp || splatterComponent.life < 0f)
            engine.removeEntity(entity)
    }
}