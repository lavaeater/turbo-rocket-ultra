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

        So, this happens after physics updates. So we cancel out the
         */

        val carBody = entity.body()
        val currentRightNormal = carBody.getWorldVector(Vector2.X)
        val latVelocity = currentRightNormal.scl(carBody.linearVelocity.dot(currentRightNormal))

        carBody.applyLinearImpulse(latVelocity.scl(-1f * carBody.mass), carBody.worldCenter, true)


        handleInput()


    }

    private fun handleInput() {

    }
}