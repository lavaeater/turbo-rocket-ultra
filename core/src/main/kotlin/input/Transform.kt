package input

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import screens.angleTo

class Transform(val position: Vector2 = vec2()) {
    /*
    A class to keep track of an objects position in 2D space
     */
    val forward: Vector2 = Vector2.X.cpy()
    private val magnitude = 10f
    val aimVector = vec2()

    private val _normal = vec2()
    val normal: Vector2
        get() = run {
            _normal.set(-forward.y, forward.x)
            _normal
        }
    private val _normalPoint = vec2()
    val normalPoint
        get() = run {
            _normalPoint.set(position).add(normal.cpy().scl(magnitude))
        }

    private val _forwardPosition = vec2()
    val forwardPoint
        get() = run {
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

    fun angleTo(other: Transform): Float {
        return MathUtils.acos(
            forward.dot(other.position.cpy().sub(position).nor())) * MathUtils.radiansToDegrees
    }


}