package ecs.components

import com.badlogic.ashley.core.Component

class VehicleControlComponent(private val controlMapper: ControlMapper) : Component {
    val turning get() = controlMapper.turning
    val acceleration get() = controlMapper.thrust
}