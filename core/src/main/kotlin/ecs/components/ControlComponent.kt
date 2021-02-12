package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class PlayerControlComponent(private val controlComponent: ControlMapper) : Component {
    val firing get() = controlComponent.firing
    val aimVector get() = controlComponent.aimVector
    val turning get() = controlComponent.turning
    val walking :Float get()  { return if(stationary) 0f else controlComponent.thrust }
    var stationary = false
}

class VehicleControlComponent(private val controlComponent: ControlMapper) : Component {
    val turning get() = controlComponent.turning
    val acceleration get() = controlComponent.turning
}

class ControlMapper {

    fun stopFiring() {
        firing = false
    }

    fun startFiring() {
        firing = true
    }

    fun throttle(amount: Float) {
        thrust = amount
    }

    fun turnA(amount: Float) {
        turning = amount
    }

    fun aimAt(unitVector: Vector2) {
        aimVector.set(unitVector)
    }

    fun mouseVector(x:Float, y:Float) {
        mousePosition.set(x, y)
    }

    val mousePosition = vec2(0f, 0f)

    val aimVector: Vector2 = vec2(0f,0f)

    var firing: Boolean = false
    var turning: Float = 0f
    var thrust: Float = 0f
}

