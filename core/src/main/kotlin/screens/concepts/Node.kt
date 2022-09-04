package screens.concepts

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import isometric.toIsoFrom3d
import ktx.math.plus
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

open class Node(var name: String, val localPosition: Vector3, var yaw: Float, var pitch: Float, var roll: Float, var color: Color = Color.RED) {
    val forward = Vector3(0f, 0f, -1f)
    val up = Vector3(0f, 1f, 0f)
    val right = Vector3(-1f, 0f, 0f)
    val children = mutableSetOf<Node>()
    var parent: Node? = null
    val position: Vector3 get() = if(parent == null) localPosition else parent!!.position + localPosition
    init {
        val initialRotation = Quaternion().setEulerAngles(yaw, pitch, roll) //Might work?
        initialRotation.transform(forward)
        initialRotation.transform(up)
        initialRotation.transform(right)
    }
    fun addChild(child: Node) {
        child.parent = this
        children.add(child)
    }

    fun removeChild(child: Node) {
        if (children.remove(child)) {
            child.parent = null
        }
    }

    val origin = vec2()
    val destination = vec2()
    fun renderIso(shapeDrawer: ShapeDrawer) {
        origin.toIsoFrom3d(position)
        shapeDrawer.setColor(color)
        shapeDrawer.filledCircle(origin, 1f)
        destination.toIsoFrom3d(forward).scl(10f)
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, 1f)
        destination.toIsoFrom3d(up).scl(10f)
        shapeDrawer.setColor(Color.GREEN)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, 1f)
        destination.toIsoFrom3d(right).scl(10f)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(origin, origin + destination)
        shapeDrawer.filledCircle(origin + destination, 1f)
    }
}