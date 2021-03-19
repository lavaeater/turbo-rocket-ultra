package ecs.components

import com.badlogic.ashley.core.Component
import ktx.math.vec2

class PlayerControlComponent(private val controlMapper: ControlMapper, val rof: Float = 1f) : Component {
    var lastShot = 0f
    val firing get() = controlMapper.firing
    val aimVector get() = controlMapper.aimVector
    var latestHitPoint = vec2(0f,0f)
    val turning : Float get() { return if(stationary) 0f else controlMapper.turning }
    val walking : Float get()  { return if(stationary) 0f else controlMapper.thrust }
    var stationary = false
}