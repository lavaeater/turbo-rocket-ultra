package screens.concepts

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import isometric.toIsoFrom3d
import ktx.math.plus
import ktx.math.unaryMinus
import ktx.math.vec2
import ktx.math.vec3
import screens.stuff.selectRecursive
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

open class Segment(name: String, localPosition: Vector3, var length:Float, val boneDirection3d: Direction3d, color: Color): Node(name, localPosition, color = color) {
    var boneEnd: Vector3 = vec3()
        private set
    get() {
        field.set(boneDirection3d.forward).scl(length)
        return field
    }
    override fun renderIso(shapeDrawer: ShapeDrawer, scale: Float) {
        super.renderIso(shapeDrawer, scale)
        origin.toIsoFrom3d(position)
        boneDirection3d.renderIso(origin, shapeDrawer, scale)
        destination.toIsoFrom3d(position + boneEnd)
        shapeDrawer.line(origin, destination)
    }

    override fun rotate(q: Quaternion) {
        super.rotate(q)
        boneDirection3d.rotate(q)
    }
}

open class Node(var name: String, val localPosition: Vector3, yaw: Float = 0f, pitch: Float = 0f, roll: Float = 0f, var color: Color = Color.RED) {
    val direction = Direction3d(yaw, pitch, roll)
    val forward get() = direction.forward
    val up get() = direction.up
    val right get() = direction.right
    val children = mutableSetOf<Node>()
    var parent: Node? = null
    val position: Vector3 get() = if(parent == null) localPosition else parent!!.position + localPosition
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
    open fun renderIso(shapeDrawer: ShapeDrawer, scale: Float = 40f) {
        for(child in children)
            child.renderIso(shapeDrawer)

        origin.toIsoFrom3d(position)
        shapeDrawer.setColor(color)
        shapeDrawer.filledCircle(origin, 1f)
        direction.renderIso(origin, shapeDrawer, scale)
    }

    fun rotateAroundUp(degrees: Float) {
        val q = Quaternion(up, degrees)
        rotate(q)
    }
    fun rotateAroundRight(degrees: Float) {
        val q = Quaternion(right, degrees)
        rotate(q)
    }
    fun rotateAroundForward(degrees: Float) {
        val q = Quaternion(forward, degrees)
        rotate(q)
    }
    fun rotateAroundY(degrees: Float) {
        val q = Quaternion(Vector3.Y, degrees)
        rotate(q)
    }
    fun rotateAroundX(degrees: Float) {
        val q = Quaternion(Vector3.X, degrees)
        rotate(q)
    }
    fun rotateAroundZ(degrees: Float) {
        val q = Quaternion(Vector3.Z, degrees)
        rotate(q)
    }

    open fun rotate(q: Quaternion) {
        direction.rotate(q)
        q.transform(localPosition)
        for(child in children) {
            child.rotate(q)
        }
    }

    val flatChildren get() = toMap()

    open fun toMap(): Map<String, Node> {
        return listOf(
            this,
            *children.asSequence().selectRecursive { children.asSequence() }.toList().toTypedArray()
        ).associateBy { it.name }
    }
}