package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Body
import eater.ecs.ashley.components.Box2d
import eater.physics.body
import eater.physics.forwardNormal
import eater.physics.forwardVelocity
import eater.physics.lateralVelocity
import ecs.components.VehicleControlComponent
import ktx.ashley.allOf
import ktx.math.times

class VehicleControlSystem : IteratingSystem(
    allOf(
        VehicleControlComponent::class,
        Box2d::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val carBody = entity.body()
        handleFriction(carBody)

        val vehicleControl = VehicleControlComponent.get(entity)
        handleInput(vehicleControl, carBody)
    }

    private fun handleFriction(carBody: Body) {
        val latVelocity = carBody.lateralVelocity()
        val skidImpulse = latVelocity.scl(-1f * carBody.mass)
        val maxImpulse = 0.1f
        if (skidImpulse.len() > maxImpulse)
            skidImpulse.scl(maxImpulse / skidImpulse.len())

        carBody.applyLinearImpulse(skidImpulse, carBody.worldCenter, true)
        carBody.applyAngularImpulse(0.1f * carBody.inertia * -carBody.angularVelocity, true)

        val forwardNormal = carBody.forwardVelocity()
        val currentForwardSpeed = forwardNormal.len()
        val dragForceMagnitude = -2 * currentForwardSpeed
        carBody.applyForce(carBody.forwardVelocity() * dragForceMagnitude, carBody.worldCenter, true)
    }

    private fun handleInput(vehicleControl: VehicleControlComponent, carBody: Body) {
        if (vehicleControl.turning != 0f) {
            carBody.applyTorque(vehicleControl.torque * vehicleControl.turning, true)
        }

        if (vehicleControl.acceleration != 0f)
            carBody.applyForceToCenter(carBody.forwardNormal().scl(vehicleControl.maxThrust * vehicleControl.acceleration), true)
    }
}