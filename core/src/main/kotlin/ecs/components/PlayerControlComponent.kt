package ecs.components

import com.badlogic.ashley.core.Component

class PlayerControlComponent(private val controlMapper: ControlMapper) : Component {
    val firing get() = controlMapper.firing
    val aimVector get() = controlMapper.aimVector
    val turning : Float get() { return if(stationary) 0f else controlMapper.turning }
    val walking : Float get()  { return if(stationary) 0f else controlMapper.thrust }
    var stationary = false
}