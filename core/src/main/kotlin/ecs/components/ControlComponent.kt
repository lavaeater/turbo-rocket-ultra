package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

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

    fun turn(amount: Float) {
        wheelAngle = amount
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
    var wheelAngle: Float = 0f
        private set
    var thrust: Float = 0f
        private set
}

