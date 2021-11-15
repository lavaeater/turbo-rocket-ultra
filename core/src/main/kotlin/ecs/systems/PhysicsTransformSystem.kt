package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.gameplay.NewTransformComponent
import ktx.ashley.allOf
import physics.getComponent

class PhysicsTransformSystem: IteratingSystem(allOf(BodyComponent::class, NewTransformComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tc = entity.getComponent<NewTransformComponent>()
        val body = entity.getComponent<BodyComponent>().body!!
        tc.position.set(body.position)
        tc.setRotationRad(body.angle)
    }
}