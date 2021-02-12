package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class PlayerControlComponent : Component {
    var firing = false
    var aimAngle = 0f
    var turning = 0f
    var walking = 0f
    var stationary = false
}

class VehicleControlComponent : Component {
    var turning = 0f
    var accelerating = 0f
}

class ControlComponent : Component {

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
        angleA = amount
    }

    fun aimAt(unitVector: Vector2) {
        aimVector.set(unitVector)
    }

    fun mouseVector(x:Float, y:Float) {
        mousePosition.set(x, y)
    }

    val mousePosition = vec2(0f, 0f)
    var aimVector: Vector2 = vec2(0f,0f)
        private set

    var firing: Boolean = false
        private set
    var angleA: Float = 0f
        private set
    var thrust: Float = 0f
        private set
}

