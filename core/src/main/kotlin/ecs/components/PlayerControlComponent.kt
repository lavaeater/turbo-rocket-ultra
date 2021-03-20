package ecs.components

import com.badlogic.ashley.core.Component
import ktx.math.vec2

class PlayerControlComponent(
    private val controlMapper: ControlMapper,
    val rof: Float = 5f) : Component {
    fun coolDown(deltaTime: Float) {
        cooldownRemaining-=deltaTime
        cooldownRemaining = cooldownRemaining.coerceAtLeast(0f)
    }

    fun shoot() {
        cooldownRemaining += 1f/rof
    }

    var cooldownRemaining = 0f
    private set


    val firing get() = controlMapper.firing && cooldownRemaining <= 0f
    val aimVector get() = controlMapper.aimVector
    var latestHitPoint = vec2(0f,0f)
    val turning : Float get() { return if(stationary) 0f else controlMapper.turning }
    val walking : Float get()  { return if(stationary) 0f else controlMapper.thrust }
    var stationary = false
}