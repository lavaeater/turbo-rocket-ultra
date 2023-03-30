package screens.concepts

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import isometric.toIsoFrom3d
import ktx.math.plus
import ktx.math.unaryMinus
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

/**
 * The orientation should perhaps never change when we rotate stuff?
 *
 * Or what do I mean here? Well, I mean that the only use of the "forward"
 * vector is to rotate around IT, but that is already handled by the bone vector itself.
 *
 * So the bone or thing should have an anchor, this is what we call the "localPosition".
 *
 * The bone then has a bone-direction-vector that is a unit vector in some direction.
 *
 * It then also has an OrientationNode - lets call everything nodes, OK? Nodes of nodes and nodes.
 *
 * When we rotate things, we want to rotate their LOCATIONS, not their ORIENTATIONS. That is handled...
 * how?
 *
 * Then to do IK, or an approximation of it, we just have to go from node to node and turn them as much
 * as possible, in the planes they can rotate, towards the target point.
 *
 * Perhaps.
 */

open class Direction3d(val yaw: Float = 0f, val pitch: Float = 0f, val roll: Float = 0f) {
    val forward = Direction.Forward()
    val up = Direction.Up()
    val right = Direction.Right()
    val left get() = -right
    val down get() = -up
    val back get() = -forward

    /*
    I need some goddamned angles here!

    I need to know every directions angle against the planes of the directions themselves, if you know what I mean.

    So, the angle between the PLANE xz and a FORWARD vector is the angle between two vectors
    1,0 (x = 1 and y = 0, where x is the z-forward (-1 really) and then a vector made from the
    forward-vectors z and y components

    Basically we calculate, in this case, the PITCH of the forward vector (can be done with any vector, I guess, or any direction,
    if you know what I mean.
     */
    private val vectorToCheck = vec2()
    private val planeVector = vec2()

    /**
     * Pitch is the rotation of the forward vector's
     *
     */
    val currentPitch: Float get() {
        vectorToCheck.set(up.z, up.y)
        planeVector.set(0f, 1f)
        return vectorToCheck.angleDeg(planeVector)
    }

    /**
     * This is the rotation of the forward-vector's x and z component
     * compared to the xz-plane with z as -1
     */
    val currentYaw: Float get() {
        vectorToCheck.set(forward.z, forward.x)
        planeVector.set(-1f, 0f)
        return vectorToCheck.angleDeg(planeVector)
    }

    /**
     * Roll is conceptually weird, but it should be, instead,
     * the left vectors' angle against the xz plane with x as 1
     *
     */
    val currentRoll: Float get() {
        vectorToCheck.set(right.x, right.y)
        planeVector.set(1f, 0f)
        return vectorToCheck.angleDeg(planeVector)
    }

    companion object {
        val thePlanes = Direction3d()
    }

    /**
     * We need some kind of values for the angles, to be able to reset directions etc.
     *
    velocity.add(tmpV.set(acceleration).scl(delta));
    position.add(tmpV.set(velocity).scl(delta));
    final float speed = velocity.len();
    final float angle = speed*delta*MathUtils.radiansToDegrees;
    Vector3 axis = tmpV.set(velocity).scl(-1f/speed).crs(Vector3.Y);
    tmpQ.set(axis, angle);
    rotation.mulLeft(tmpQ);
    transform.set(position, rotation);
     *
     */

    fun reset() : Quaternion {
        /**
         * To reset, just create a quaternion from the input values and
         * use it to rotate this direction. Doh!
         *
         */
        val rotateQuaternion = Quaternion().setFromCross(forward, Vector3.Y).mul(Quaternion().setFromCross(forward, Vector3.X)).mul(Quaternion().setFromCross(forward, Vector3.Z))

        val y = -rotateQuaternion.getAngleAround(Vector3.Y)
        val x = -rotateQuaternion.getAngleAround(Vector3.X)
        val z = -rotateQuaternion.getAngleAround(Vector3.Z)

        val antiQuat = Quaternion(Vector3.Y, y).mul(Quaternion(Vector3.X, x)).mul(Quaternion(Vector3.Z, z))
        return antiQuat
    }

    init {
        val initialRotation = Quaternion().setEulerAngles(yaw, pitch, roll)
        rotate(initialRotation)
    }

    fun rotate(q: Quaternion) {
        q.transform(forward)
        q.transform(up)
        q.transform(right)
    }

    private val destination = vec2()

    fun renderIso(origin: Vector2, shapeDrawer: ShapeDrawer, scale: Float = 100f) {
        destination.toIsoFrom3d(forward.cpy().scl(scale))
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, .25f)
        destination.toIsoFrom3d(up.cpy().scl(scale))
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, .25f)
        destination.toIsoFrom3d(right.cpy().scl(scale))
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, .25f)
    }
}