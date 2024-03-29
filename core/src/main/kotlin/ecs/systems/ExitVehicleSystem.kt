package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.IsInVehicleComponent
import ecs.components.LeaveVehicleComponent
import ktx.ashley.allOf
import ktx.ashley.remove

/*
Needs some info to remove the control component from the actual vehicle entity as well.
 */
class ExitVehicleSystem: IteratingSystem(
    allOf(
        LeaveVehicleComponent::class,
        IsInVehicleComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.remove<IsInVehicleComponent>()
        entity.remove<LeaveVehicleComponent>()
    }
}