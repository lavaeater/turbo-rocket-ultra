package screens.stuff

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import isometric.toIsometric
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import screens.concepts.DirtyClass
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.sqrt
import kotlin.properties.Delegates.observable

/**
 * We need a skeleton in 3d, that's just it.
 *
 * So, a bone is a line in space, I would imagine.
 *
 * A line in 3d space has a start and an end. So to connect bones we need hierarchies,
 * so, a bone is localpoints (offsets from zero), and has parents and children.
 *
 * This is the way.
 *
 * The skeleton could be separate from everything else, I suppose.
 */


fun Node3d.flatAndSorted(): List<Node3d> {
    return listOf(
        this,
        *children.asSequence().selectRecursive { children.asSequence() }.toList().toTypedArray()
    ).sortedWith(compareBy<Node3d> { -it.globalPosition3d.z }.thenBy { it.globalPosition3d.y })//.thenBy { it.globalPosition3d.z })
//    )// .sortedBy { it.globalPosition3d.z }
}

fun <T> Sequence<T>.selectRecursive(recursiveSelector: T.() -> Sequence<T>): Sequence<T> = flatMap {
    sequence {
        yield(it)
        yieldAll(it.recursiveSelector().selectRecursive(recursiveSelector))
    }
}

fun Vector3.toIso() : Vector2 {
    return vec2((this.x - this.z) / sqrt(2f), (this.x + 2 * this.y + this.z) / sqrt(6f))
}


open class Node3d(
    name: String,
    localPosition: Vector3 = vec3(),
    parent: Node3d? = null,
    color: Color = Color.WHITE,
    val updateActions: MutableList<(Node3d, Float) -> Unit> = mutableListOf()
) : DirtyClass() {
    var color: Color by observable(color, ::setDirty)
    var rotateWithParent: Boolean by observable(true, ::setDirty)
    var parent: Node3d? by observable(parent, ::setDirty)
    var globalPosition3d by observable(vec3(), ::setDirty)
    var localPosition3d by observable(localPosition, ::setDirty)
    val children = mutableListOf<Node3d>()
    private val rotationVector = vec2(0f,1f)

    fun rotateBy(degrees: Float) {
        if(!MathUtils.isZero(degrees)) {
            rotationVector.rotateAroundDeg(Vector2.Zero, degrees)
            setDirty()
        }
    }
    val rotation: Float
        get() = rotationVector.angleDeg()

    override fun setDirty() {
        super.setDirty()
        for (childNode in children) {
            childNode.setDirty()
        }
    }

    private fun rotate() {
        /* The rotation should, obviously, be a 2d-vector rotating around the parents position in a specific
            plane, namely the x-z-plane (we are rotating around the Y-axis)
             */
        if(rotateWithParent && parent != null) {
            rotateBy(parent!!.rotation - rotation)
            globalPosition3d.rotate(Vector3.Y, parent!!.rotation)
        }
    }


    fun addChild(child: Node3d) {
        child.parent = this
        children.add(child)
        setDirty()
    }

    fun removeChild(child: Node3d): Boolean {
        return if (children.remove(child)) {
            child.parent = null
            setDirty()
            true
        } else false
    }

    val isoPosition = vec2()

    fun calcIsoPosition() {
        isoPosition.x = (globalPosition3d.x - globalPosition3d.z) / sqrt(2f)
        isoPosition.y = (globalPosition3d.x + 2 * globalPosition3d.y + globalPosition3d.z) / sqrt(6f)
    }

    fun update(delta: Float) {
        updateActions.forEach {it(this, delta) }
        if (dirty) {
            updatePosition3d()
            rotate()
            calcIsoPosition()

            dirty = false

//            if (parent != null) {
//                actualPosition.set(parent!!.actualPosition + position.toMutable())
//                if (rotateWithParent) {
//                    actualPosition.rotateAroundDeg(parent!!.actualPosition, parent!!.rotation)
//                    rotation = actualPosition.angleDeg()
//                }
//            } else {
//                actualPosition.set(position.toMutable())
//            }
        }
//        for (childNode in children) {
//            childNode.update(delta)
//        }
        for (childNode in children) {
            childNode.update(delta)
        }
    }

    private fun updatePosition3d() {
        if (parent == null) {
            globalPosition3d.set(localPosition3d)
        } else {
            globalPosition3d.set(localPosition3d + parent!!.globalPosition3d)
        }
    }

    open fun drawIso(
        batch: Batch,
        shapeDrawer: ShapeDrawer,
        delta: Float,
        recursive: Boolean = true,
        offset: Vector2 = Vector2.Zero
    ) {
        shapeDrawer.filledCircle(isoPosition + offset.toIsometric(), 1f, color)
        if (recursive)
            for (childNode in children) {
                childNode.drawIso(batch, shapeDrawer, delta, true, offset)
            }
    }

    open fun draw2d(
        batch: Batch,
        shapeDrawer: ShapeDrawer,
        delta: Float,
        recursive: Boolean = true,
        offset: Vector2 = Vector2.Zero,
        zUp: Boolean = false
    ) {
        if (zUp)
            shapeDrawer.filledCircle(globalPosition3d.x + offset.x, globalPosition3d.y + offset.y, 5f, color)
        else
            shapeDrawer.filledCircle(globalPosition3d.x + offset.x, globalPosition3d.y + offset.y, 5f, color)

        if (recursive)
            for (childNode in children) {
                childNode.draw2d(batch, shapeDrawer, delta, true, offset, zUp)
            }
    }
}