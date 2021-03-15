package ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.physics.box2d.World

class PhysicsSystem(private val world: World, private val timeStep : Float = 1/120f) :
    IntervalSystem(timeStep) {

    private val velIters = 2
    private val posIters = 2

    override fun updateInterval() {
        world.step(timeStep, velIters, posIters)
    }
}