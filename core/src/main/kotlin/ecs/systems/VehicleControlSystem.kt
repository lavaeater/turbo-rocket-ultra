package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ecs.components.BodyComponent
import ecs.components.ControlComponent
import ecs.components.VehicleComponent
import injection.Context.inject
import ktx.ashley.allOf
import physics.body

/**
 * Should probably be split into several systems
 * One for input,
 * one for friction and other forces...
 */


class VehicleControlSystem: IteratingSystem(
    allOf(
        VehicleComponent::class,
        BodyComponent::class).get()) {

    val controller by lazy { inject<ControlComponent>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        http://www.iforce2d.net/b2dtut/top-down-car

        We will eliminate tyres, because... why have them? Or add them if necessary.

        Anyways, we will follow this article and try to make the car behave "realistically"

        So, this happens after physics updates. So we cancel out the sideways and angular
        velocitites - but we shouldn't actually do that. That is only because the tutorial
        eliminates the angular velocity, but we want angular velocity to be a thing...

        Perhaps we should actually have tyres? To make all this slightly simpler?

        Why am I doing a car, again? Is there some highter purpose to all of this? Yes,
        it is exploration of game mechanics

         */

        val carBody = entity.body()
        val currentRightNormal = carBody.getWorldVector(Vector2.X)
        val latVelocity = currentRightNormal.scl(carBody.linearVelocity.dot(currentRightNormal))

        carBody.applyLinearImpulse(latVelocity.scl(-1f * carBody.mass), carBody.worldCenter, true)
        carBody.applyAngularImpulse(0.1f * carBody.inertia * -carBody.angularVelocity, true )



        handleInput()


    }

    private fun handleInput() {

    }
}