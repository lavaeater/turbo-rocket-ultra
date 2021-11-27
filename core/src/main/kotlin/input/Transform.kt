package input

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import injection.Context.inject
import ktx.math.vec2
import ktx.math.vec3

/**
 * This transform class i pretty great for thrusting games, which this
 * obviously isn't. So what can we use it for then, eh?
 *
 * Aiming and stuff, of course. On the other hand, if I made
 * the methods for checking fields of View into functions just
 * available wherever, then the need for this class might be
 * diminished.
 *
 * I know I have a working implementation of dot product for field of
 * view, so let's use that for now.
 */
open class Transform(val position: Vector2 = vec2()) {
    /*
    A class to keep track of an objects position in 2D space
     */
    val forward: Vector2 = Vector2.X.cpy()
    private val magnitude = 10f
    val aimVector = vec2()
    private val mousePosition3D = vec3()
    private val camera by lazy { inject<OrthographicCamera>() }
    private val mousePosition = vec2()

    private val _normal = vec2()
    val normal: Vector2
        get() = run {
            _normal.set(-forward.y, forward.x)
            _normal
        }
    private val _normalPoint = vec2()
    val normalPoint: Vector2
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

    fun setAimVectorTo(aimVector: Vector2) {
        aimVector.set(aimVector)
    }

    fun pointAimVectorAt(worldAimPoint: Vector2) {
        aimVector.set(worldAimPoint).sub(position).nor()
    }

    fun pointAimVectorAtScreeCoords(screenX: Int, screenY: Int) {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition.set(mousePosition3D.x, mousePosition3D.y)
        pointAimVectorAt(mousePosition)
    }

    fun setRotationRad(angleRad: Float) {
        forward.setAngleRad(angleRad)
    }

    fun setRotationDeg(angleDeg: Float) {
        forward.setAngleDeg(angleDeg)
    }

    fun rotate(angleDeg: Float) {
        forward.rotateDeg(angleDeg)
    }

    fun forwardAngleTo(other: Transform): Float {
        return MathUtils.acos(
            forward.dot(other.position.cpy().sub(position).nor())) * MathUtils.radiansToDegrees
    }

    fun aimVectorAngleTo(other: Transform): Float {
        return MathUtils.acos(
            aimVector.dot(other.position.cpy().sub(position).nor())) * MathUtils.radiansToDegrees
    }

    /**
     * Checks if you are inside the field of view,
     * supplied in degrees, from the perspective of the
     * AimVector, which is where the entity most likely is looking.
     */
    fun canISeeYou(you: Transform, fovDeg: Float): Boolean {
        return aimVectorAngleTo(you) < fovDeg / 2
    }
}

fun canISeeYouFromHere(from: Vector2, aimVector: Vector2, to:Vector2, fovDeg: Float): Boolean {
    return angleToDeg(from, aimVector, to) < fovDeg / 2
}

fun angleToDeg(from: Vector2, aimVector: Vector2, to: Vector2) : Float {
    return MathUtils.acos(
        aimVector.dot(to.cpy().sub(from).nor())) * MathUtils.radiansToDegrees
}