package ecs.systems.fx

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.fx.CreateEntityComponent
import ktx.ashley.allOf
import eater.physics.getComponent

class DelayedEntityCreationSystem: IteratingSystem(allOf(CreateEntityComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val creator = entity.getComponent<CreateEntityComponent>()
        creator.creator()
        engine.removeEntity(entity)
    }

}