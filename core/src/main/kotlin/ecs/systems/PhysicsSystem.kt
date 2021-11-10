package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import physics.getComponent

class PhysicsSystem(private val world: World, private val timeStep : Float = 1/60f) :
    IteratingSystem(allOf(BodyComponent::class, TransformComponent::class).get()) {

    private val velIters = 2
    private val posIters = 2

    var accumulator = 0f

    override fun update(deltaTime: Float) {
        val ourTime = deltaTime.coerceAtMost(timeStep * 2)
        accumulator += ourTime
        while(accumulator > timeStep) {
            world.step(timeStep, velIters, posIters)
            accumulator -= ourTime
        }
        super.update(deltaTime)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
            val bodyComponent = entity.getComponent<BodyComponent>()
            val bodyPosition = bodyComponent.body!!.position
            val bodyRotation = bodyComponent.body!!.angle
            val transformComponent = entity.getComponent<TransformComponent>()
            transformComponent.position.set(bodyPosition)
            transformComponent.rotation = bodyRotation

    }

}