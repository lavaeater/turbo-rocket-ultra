package eater.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.core.world
import eater.ecs.ashley.components.Box2d
import eater.ecs.ashley.components.Remove
import eater.ecs.ashley.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude

class Box2dUpdateSystem(private val timeStep: Float, private val velIters: Int, private val posIters: Int): IteratingSystem(
    allOf(Box2d::class, TransformComponent::class).exclude(Remove::class).get()) {
    var accumulator = 0f
    override fun update(deltaTime: Float) {
        val ourTime = deltaTime.coerceAtMost(timeStep * 2)
        accumulator += ourTime
        while (accumulator > timeStep) {
            world().step(timeStep, velIters, posIters)
            accumulator -= ourTime
        }
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        TransformComponent.get(entity).update(Box2d.get(entity).body)
    }
}