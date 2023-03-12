package eater.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.LightComponent
import eater.ecs.ashley.components.Remove
import eater.ecs.ashley.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude

class LightPositionUpdateSystem: IteratingSystem(
    allOf(
        LightComponent::class,
        TransformComponent::class
    ).exclude(Remove::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val lightComponent = LightComponent.get(entity)
        val transform = TransformComponent.get(entity)
        lightComponent.light.setPosition(transform.position.x, transform.position.y)
    }
}