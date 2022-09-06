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

sealed class Direction(v: Vector3) : Vector3(v.x, v.y, v.z) {
    class Up() : Direction(vec3(0f, 1f, 0f))
    class Down() : Direction(vec3(0f, -1f, 0f))
    class Forward() : Direction(vec3(0f, 0f, -1f))
    class Back() : Direction(vec3(0f, 0f, 1f))
    class Right() : Direction(vec3(-1f, 0f, 0f))
    class Left() : Direction(vec3(1f, 0f, 0f))
}

sealed class RotationDirection() {
    fun getTargetRotation(node: Node, degrees: Float) : Float {
        var targetDegrees = degrees
        val currentRotation = node.rotations[this]!!
        val range = node.rotationDirections[this]!!
        if(!range.contains(currentRotation)) {
            targetDegrees = if(currentRotation < range.start) {
                range.start - currentRotation
            } else {
                range.endInclusive - currentRotation
            }
        } else if(!range.contains(currentRotation + targetDegrees)) {
            targetDegrees = if(targetDegrees < 0f) {
                range.start - currentRotation
            } else {
                range.endInclusive - currentRotation
            }
        }
        return targetDegrees
    }

    open fun rotate(node: Node, degrees: Float) {
        val targetDegrees = getTargetRotation(node, degrees)
        setRotation(node, targetDegrees)
        val q = Quaternion(rotationFuncs[this]!!(node), targetDegrees)
        node.rotate(q)
    }
    fun setRotation(node: Node, targetDegrees: Float) {
        node.rotations[this] = node.rotations[this]!! + targetDegrees
    }
    object AroundUp : RotationDirection()
    object AroundRight : RotationDirection()

    object AroundForward : RotationDirection()

    object AroundY : RotationDirection()

    object AroundX : RotationDirection()

    object AroundZ : RotationDirection()

    object AroundParentUp : RotationDirection()

    object AroundParentForward : RotationDirection()

    object AroundParentRight : RotationDirection()
    object AroundParentCross : RotationDirection()

    companion object {
        val fullRange = 0f..360f
        val allRotations = mapOf(
            AroundUp to fullRange,
            AroundRight to fullRange,
            AroundForward to fullRange,
            AroundY to fullRange,
            AroundX to fullRange,
            AroundZ to fullRange,
            AroundParentUp to fullRange,
            AroundParentRight to fullRange,
            AroundParentForward to fullRange,
            AroundParentCross to fullRange
        )
        val rotationFuncs = mapOf<RotationDirection, (Node) -> Vector3>(
            AroundY to { _ -> Vector3.Y },
            AroundX to { _ -> Vector3.X },
            AroundZ to { _ -> Vector3.Z },
            AroundUp to { node -> node.up },
            AroundForward to { node -> node.forward },
            AroundRight to { node -> node.right },
            AroundParentForward to { node -> if(node.parent == null) node.forward else node.parent!!.forward },
            AroundParentRight to { node -> if(node.parent == null) node.right else node.parent!!.right },
            AroundParentUp to { node -> if(node.parent == null) node.up else node.parent!!.up },
            AroundParentCross to { node -> if(node.parent == null) node.right else {
                val rotVec = node.parent!!.forward.cpy().crs(node.forward).nor()
                if(rotVec.epsilonEquals(Vector3.Zero)) {
                    node.parent!!.right
                } else rotVec
            } },
        )
    }
}

open class Node(
    var name: String,
    val localPosition: Vector3 = vec3(),
    yaw: Float = 0f,
    pitch: Float = 0f,
    roll: Float = 0f,
    var color: Color = Color.RED,
    var scale: Float = 1f,
    val rotationDirections: Map<RotationDirection, ClosedFloatingPointRange<Float>> = RotationDirection.allRotations
) {
    var direction = Direction3d(yaw, pitch, roll)
    val forward get() = direction.forward
    val up get() = direction.up
    val right get() = direction.right
    val children = mutableSetOf<Node>()
    var parent: Node? = null
    val rotations = rotationDirections.map { it.key to 0f }.toMap().toMutableMap()
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
        //direction.renderIso(origin, shapeDrawer, scale)
    }

    fun rotate(direction: RotationDirection, degrees: Float) {
        if (rotationDirections.contains(direction)) {
            direction.rotate(this, degrees)
        }
    }

    fun rotateAroundParentUp(degrees: Float) {
        if (parent == null) {
            rotateAroundUp(degrees)
        } else {
            val q = Quaternion(parent!!.direction.up, degrees)
            rotate(q)
        }
    }

    fun rotateAroundParentForward(degrees: Float) {
        if (parent == null) {
            rotateAroundForward(degrees)
        } else {
            val q = Quaternion(parent!!.direction.forward, degrees)
            rotate(q)
        }
    }

    fun rotateAroundParentRight(degrees: Float) {
        if (parent == null) {
            rotateAroundRight(degrees)
        } else {
            val q = Quaternion(parent!!.direction.right, degrees)
            rotate(q)
        }
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

    /**
     * Final thoughts:
     *
     * Reduce back to less. Explore models consisting of Three segments
     * and their joints respective to each other and how to handle them.
     *
     * We should always only ever need THREE rotations in some fashion,
     * yaw, pitch and roll.
     */


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