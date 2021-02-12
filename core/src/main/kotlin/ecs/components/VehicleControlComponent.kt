package ecs.components

import com.badlogic.ashley.core.Component

class VehicleControlComponent(
    private val controlMapper: ControlMapper,
    val maxForwardSpeed: Float = 100f,
    val maxBackwardSpeed: Float = 20f,
    val maxThrust: Float = 5000f,
    val torque : Float = 25f
) : Component {
    val turning get() = controlMapper.turning
    val acceleration get() = controlMapper.thrust
}