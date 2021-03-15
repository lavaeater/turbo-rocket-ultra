package ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.physics.box2d.World

class PhysicsSystem(private val world: World, private val timeStep : Float = 1/75f) :
    EntitySystem() {

    private val velIters = 2
    private val posIters = 2

    var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while(accumulator > timeStep) {
            world.step(timeStep, velIters, posIters)
            accumulator -= timeStep
        }
    }

}