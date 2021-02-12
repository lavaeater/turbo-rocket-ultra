package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.*
import factories.vehicle
import ktx.ashley.allOf
import ktx.ashley.hasNot
import ktx.ashley.mapperFor
import ktx.ashley.remove
import ktx.box2d.revoluteJointWith
import physics.playerControlComponent

class EnterVehicleSystem : IteratingSystem(allOf(EnterVehicleComponent::class, PlayerControlComponent::class).get()){
    private val bodyMapper = mapperFor<BodyComponent>()
    private val isInVehicleMapper = mapperFor<IsInVehicleComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.hasNot(isInVehicleMapper)) {
            entity.add(IsInVehicleComponent())
            entity.playerControlComponent().stationary = true
            val pBody = bodyMapper.get(entity).body
            val vBody = vehicle(pBody.position)
            vBody.revoluteJointWith(pBody) {
                initialize(vBody, pBody, pBody.worldCenter)
            }
        }
    }
}

class ExitVehicleSystem : IteratingSystem(allOf(LeaveVehicleComponent::class, IsInVehicleComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.remove<IsInVehicleComponent>()
        entity.remove<LeaveVehicleComponent>()
        entity.playerControlComponent().stationary = false
    }
}