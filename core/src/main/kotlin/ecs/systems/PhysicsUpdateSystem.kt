package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PhysicsUpdateSystem() : IteratingSystem(
    allOf(
        BodyComponent::class,
        TransformComponent::class
    ).get()) {

    private val bodyMapper = mapperFor<BodyComponent>()
    private val transMapper = mapperFor<TransformComponent>()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val bodyComponent = bodyMapper.get(entity)!!
        val bodyPosition = bodyComponent.body.position
        val bodyRotation = bodyComponent.body.angle
        val transformComponent = transMapper.get(entity)!!
        transformComponent.position.set(bodyPosition)
        transformComponent.rotation = bodyRotation
    }
}