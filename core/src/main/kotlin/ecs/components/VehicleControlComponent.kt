package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import input.ControlMapper
import ktx.ashley.mapperFor

class VehicleControlComponent(
    private val controlMapper: ControlMapper,
    val maxForwardSpeed: Float = 100f,
    val maxBackwardSpeed: Float = 20f,
    val maxThrust: Float = 1000f,
    val torque : Float = 25f
) : Component {
    val turning get() = controlMapper.turning
    val acceleration get() = controlMapper.thrust

    companion object {
        val mapper = mapperFor<VehicleControlComponent>()
        fun get(entity: Entity): VehicleControlComponent {
            return mapper.get(entity)
        }

        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
    }
}