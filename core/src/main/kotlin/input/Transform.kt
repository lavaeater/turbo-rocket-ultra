package input

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class Transform(val position: Vector2 = vec2()) {
    /*
    A class to keep track of an objects position in 2D space
     */
    val forward: Vector2 = Vector2.X.cpy()
    val magnitude = 10f

    private val _forwardPosition = vec2()
    val aimVector = vec2()
    val forwardPoint get() = run {
        _forwardPosition.set(position).add(forward.cpy().scl(magnitude))
        _forwardPosition
    }

    fun set(newPos: Vector2) {
        position.set(newPos)
    }

    fun dst2(transform: Transform): Float {
        return position.dst2(transform.position)
    }

    fun dst(transform: Transform): Float {
        return position.dst(transform.position)
    }

    fun setAimVector(mousePosition: Vector2) {
        aimVector.set(mousePosition).sub(position).nor()
    }

    fun setRotation(angleDeg: Float) {
        forward.setAngleDeg(angleDeg)
    }

    fun rotate(angleDeg: Float) {
        forward.rotateDeg(angleDeg)
    }

}