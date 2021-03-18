package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import ecs.components.BodyComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import physics.Mappers

class PhysicsSystem(private val world: World, private val timeStep : Float = 1/60f) :
    IteratingSystem(allOf(BodyComponent::class, TransformComponent::class).get()) {

    private val velIters = 2
    private val posIters = 2
    private val tMapper = Mappers.transformMapper
    private val bMapper = Mappers.bodyMapper

    var accumulator = 0f

    override fun update(deltaTime: Float) {
//        super.update(deltaTime)
        val ourTime = deltaTime.coerceAtMost(timeStep * 2)
        accumulator += ourTime
        while(accumulator > timeStep) {
            world.step(timeStep, velIters, posIters)
            for(entity in entities) {
                val bodyComponent = bMapper.get(entity)!!
                val bodyPosition = bodyComponent.body.position
                val bodyRotation = bodyComponent.body.angle
                val transformComponent = tMapper.get(entity)!!
                transformComponent.position.set(bodyPosition)
                transformComponent.rotation = bodyRotation
            }
            accumulator -= ourTime
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        //supposed to be empty - is never called because we don't call super.update in update override
    }

}