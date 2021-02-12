package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import ecs.components.BodyComponent
import ecs.components.ControlComponent
import ecs.components.VehicleComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.times
import ktx.math.vec2
import physics.body
import physics.forwardNormal
import physics.forwardVelocity
import physics.lateralVelocity

/**
 * Should probably be split into several systems
 * One for input,
 * one for friction and other forces...
 *
 * Ooh, I see.
 *
 * We have some sort of control component that
 */

class VehicleControlSystem: IteratingSystem(
    allOf(
        VehicleComponent::class,
        BodyComponent::class).get()) {

    private val controlComponent by lazy { inject<ControlComponent>() }

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
        val latVelocity = carBody.lateralVelocity()

        carBody.applyLinearImpulse(latVelocity.scl(-1f * carBody.mass), carBody.worldCenter, true)
        //carBody.applyAngularImpulse(0.1f * carBody.inertia * -carBody.angularVelocity, true )

        val forwardNormal = carBody.forwardVelocity()
        val currentForwardSpeed = forwardNormal.len()
        val dragForceMagnitude = -2 * currentForwardSpeed
        carBody.applyForce(carBody.forwardVelocity() * dragForceMagnitude, carBody.worldCenter, true );

        handleInput(carBody)
    }

    private fun handleInput(carBody: Body) {
        if (controlComponent.wheelAngle != 0f) {
            carBody.applyTorque(500f * controlComponent.wheelAngle, true)
        }

        val forceVector = vec2(MathUtils.cos(carBody.angle), MathUtils.sin(carBody.angle)).rotate90(1)

        if (controlComponent.thrust > 0f)
            carBody.applyForceToCenter( forceVector.scl(100f) * carBody.forwardNormal(), true)
            //carBody.applyForceToCenter(forceVector.scl(200f), true)
    }
}