package input

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class Transform(val position: Vector2 = vec2()) {
    /*
    A class to keep track of an objects position in 2D space
     */
    val forward: Vector2 = Vector2.X.cpy()
    val _forwardPosition = vec2()
    val aimVector = vec2()
    val forwardPosition get() = run {
        _forwardPosition.set(position).add(forward)
        _forwardPosition
    }

    fun setAimVector(mousePosition: Vector2) {
        aimVector.set(mousePosition).sub(position).nor()
    }

    fun forwardAt(distance: Float = 10f) : Vector2 {
        _forwardPosition.set(position).add(forward.cpy().scl(distance))
        return _forwardPosition
    }

    fun setRotation(angleDeg: Float) {
        forward.setAngleDeg(angleDeg)
    }

    fun rotate(angleDeg: Float) {
        forward.rotateDeg(angleDeg)
    }

}