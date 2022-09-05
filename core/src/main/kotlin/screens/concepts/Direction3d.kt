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

open class Direction3d(yaw: Float = 0f, pitch: Float = 0f, roll: Float = 0f) {
    val forward = Vector3(0f, 0f, -1f)
    val up = Vector3(0f, 1f, 0f)
    val right = Vector3(-1f, 0f, 0f)
    val left get() = -right
    val down get() = -up
    val back get() = -forward

    init {
        val initialRotation = Quaternion().setEulerAngles(yaw, pitch, roll) //Might work?
        rotate(initialRotation)
    }

    fun rotate(q: Quaternion) {
        q.transform(forward)
        q.transform(up)
        q.transform(right)
    }

    private val destination = vec2()

    fun renderIso(origin: Vector2, shapeDrawer: ShapeDrawer, scale: Float) {
        destination.toIsoFrom3d(forward).scl(scale)
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, 1f)
        destination.toIsoFrom3d(up).scl(scale)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, 1f)
        destination.toIsoFrom3d(right).scl(scale)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, 1f)
    }
}