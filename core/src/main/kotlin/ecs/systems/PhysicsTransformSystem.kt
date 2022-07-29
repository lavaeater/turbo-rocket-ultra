package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.components.Box2d
import ecs.components.gameplay.NewTransformComponent
import ktx.ashley.allOf
import eater.physics.getComponent

class PhysicsTransformSystem: IteratingSystem(allOf(Box2d::class, NewTransformComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tc = entity.getComponent<NewTransformComponent>()
        val body = entity.getComponent<Box2d>().body!!
        tc.position.set(body.position)
        tc.setRotationRad(body.angle)
    }
}