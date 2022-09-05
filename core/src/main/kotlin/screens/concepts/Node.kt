package screens.concepts

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import isometric.toIsoFrom3d
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import screens.stuff.selectRecursive
import space.earlygrey.shapedrawer.ShapeDrawer

open class Node(
    var name: String,
    val localPosition: Vector3 = vec3(),
    yaw: Float = 0f,
    pitch: Float = 0f,
    roll: Float = 0f,
    var color: Color = Color.RED,
    var scale: Float = 1f
) {
    val direction = Direction3d(yaw, pitch, roll)
    val forward get() = direction.forward
    val up get() = direction.up
    val right get() = direction.right
    val children = mutableSetOf<Node>()
    var parent: Node? = null
    open var position: Vector3 = vec3()
        protected set
        get() {
            return if (parent == null) field.set(localPosition) else field.set(parent!!.position).add(localPosition)
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
    open fun renderIso(shapeDrawer: ShapeDrawer) {
        for (child in children)
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

    fun reset() {
        val q = direction.reset()
        rotate(q)
    }

    open fun rotate(q: Quaternion) {
        direction.rotate(q)
        q.transform(localPosition)
        for (child in children) {
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