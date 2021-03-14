package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.EnterVehicleComponent
import ecs.components.IsInVehicleComponent
import ecs.components.PlayerControlComponent
import factories.vehicle
import ktx.ashley.allOf
import ktx.ashley.hasNot
import ktx.ashley.mapperFor
import ktx.box2d.revoluteJointWith
import physics.playerControlComponent

class EnterVehicleSystem : IteratingSystem(
    allOf(
        EnterVehicleComponent::class,
        PlayerControlComponent::class).get()) {

    private val bodyMapper = mapperFor<BodyComponent>()
    private val isInVehicleMapper = mapperFor<IsInVehicleComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.hasNot(isInVehicleMapper)) {
            entity.add(IsInVehicleComponent())
            entity.playerControlComponent().stationary = true
            val pBody = bodyMapper.get(entity).body
            val vBody = vehicle(pBody.worldCenter)
            pBody.revoluteJointWith(vBody) {
                localAnchorA.set(bodyA.localCenter)
                localAnchorB.set(bodyB.localCenter.cpy().set(0f, -1.5f))
            }
        }
    }
}

