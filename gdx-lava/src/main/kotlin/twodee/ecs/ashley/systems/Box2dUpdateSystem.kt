package twodee.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import twodee.core.world
import twodee.ecs.ashley.components.Box2d
import twodee.ecs.ashley.components.Remove
import twodee.ecs.ashley.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude

open class Box2dUpdateSystem(private val timeStep: Float, private val velIters: Int, private val posIters: Int): IteratingSystem(
    allOf(Box2d::class, TransformComponent::class).exclude(Remove::class).get()) {
    var accumulator = 0f
    override fun update(deltaTime: Float) {
        val ourTime = deltaTime.coerceAtMost(timeStep * 2)
        accumulator += ourTime
        while (accumulator > timeStep) {
            world().step(timeStep, velIters, posIters)
            everyTimeStep(deltaTime)
            accumulator -= ourTime
        }
        super.update(deltaTime)
    }

    open fun everyTimeStep(deltaTime: Float) {
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        TransformComponent.get(entity).update(Box2d.get(entity).body)
    }
}
