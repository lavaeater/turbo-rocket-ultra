package screens.stuff

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import isometric.toIsometric
import ktx.math.ImmutableVector2
import ktx.math.plus
import ktx.math.toMutable
import ktx.math.vec2
import screens.DirtyClass
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

/**
 * I am fucking wasting my time, again, as per usual.
 *
 * Vectors can be rotated around other points, there is already support for transformations and stuff...
 *
 * I am so tired and anxious. What is the goal here? Am I just wasting my time?
 *
 * Why have centerpoints and other bullshit, when I could just have like three vectors (points)
 * and rotate them all around the center? Wouldn't that be easier?
 *
 * Should I even do it like this, eh?
 */

open class Node : DirtyClass() {
    var color = Color.RED
    var parent: Node? by observable(null, ::setDirty)
    var position: ImmutableVector2 by observable(ImmutableVector2(0f, 0f), ::setDirty)
    var height by observable(0f, ::setDirty)
    var actualPosition = vec2()
    var rotation by observable(0f, ::setDirty)
    val children = mutableListOf<Node>()
    var rotateWithParent by observable(true, ::setDirty)
    var updateAction: (Node, Float) -> Unit = { _, _ -> }
    override fun setDirty() {
        super.setDirty()
        for (childNode in children) {
            childNode.setDirty()
        }
    }

    fun addChild(childNode: Node) {
        childNode.parent = this
        children.add(childNode)
        setDirty()
    }

    fun removeChild(childNode: Node) {
        if (children.remove(childNode)) {
            childNode.parent = null
            setDirty()
        }
    }

    fun update(delta: Float) {
        if (dirty) {
            updateAction(this, delta)
            if (parent != null) {
                actualPosition.set(parent!!.actualPosition + position.toMutable())
                if (rotateWithParent) {
                    actualPosition.rotateAroundDeg(parent!!.actualPosition, parent!!.rotation)
                    rotation = actualPosition.angleDeg()
                }
            } else {
                actualPosition.set(position.toMutable())
            }
        }
        for (childNode in children) {
            childNode.update(delta)
        }
    }

    open fun drawIso(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float) {
        shapeDrawer.filledCircle(actualPosition.toIsometric(), 1f, color)
        for (childNode in children) {
            childNode.drawIso(batch, shapeDrawer, delta)
        }
    }

    open fun draw(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float) {
        shapeDrawer.filledCircle(actualPosition, 5f, color)
        for (childNode in children) {
            childNode.draw(batch, shapeDrawer, delta)
        }
    }
}