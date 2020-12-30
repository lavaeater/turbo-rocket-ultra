package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import ecs.components.*
import factories.world
import ktx.ashley.allOf
import ktx.ashley.hasNot
import ktx.ashley.mapperFor

class EnterVehicleSystem : IteratingSystem(allOf(EnterVehicleComponent::class).get()){
    private val bodyMapper = mapperFor<BodyComponent>()
    private val isInVehicleMapper = mapperFor<IsInVehicleComponent>()
    private val vEntity: Entity by lazy { engine.getEntitiesFor(allOf(VehicleComponent::class).get()).first() }


    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.hasNot(isInVehicleMapper)) {
            entity.add(IsInVehicleComponent())
            val vBody = bodyMapper.get(vEntity).body
            val pBody = bodyMapper.get(entity).body

            //1. Teleport the player body on top of the vehicle body,
            //perhaps?

            pBody.position.set(vBody.worldCenter)

            val joint = RevoluteJointDef().apply {
                initialize(vBody, pBody, vBody.worldCenter)
            }
            world().createJoint(joint)
        }
    }
}

class ExitVehicleSystem : IteratingSystem(allOf(LeaveVehicleComponent::class, IsInVehicleComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        TODO("Not yet implemented")
    }
}