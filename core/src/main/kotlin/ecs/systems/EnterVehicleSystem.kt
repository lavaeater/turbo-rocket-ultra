package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.BodyComponent
import ecs.components.EnterVehicleComponent
import ecs.components.IsInVehicleComponent
import ecs.components.LeaveVehicleComponent
import factories.vehicle
import ktx.ashley.allOf
import ktx.ashley.hasNot
import ktx.ashley.mapperFor
import ktx.box2d.revoluteJointWith

class EnterVehicleSystem : IteratingSystem(allOf(EnterVehicleComponent::class).get()){
    private val bodyMapper = mapperFor<BodyComponent>()
    private val isInVehicleMapper = mapperFor<IsInVehicleComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.hasNot(isInVehicleMapper)) {
            entity.add(IsInVehicleComponent())
            engine.removeSystem(engine.getSystem(PlayerControlSystem::class.java))
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
        TODO("Not yet implemented")
    }
}