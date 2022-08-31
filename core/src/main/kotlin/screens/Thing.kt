package screens

import com.badlogic.gdx.math.Vector3
import ktx.math.plus
import screens.stuff.selectRecursive

abstract class Thing(override var name: String, override val localPosition: Vector3) : IThing {
    override var forwardLimit: ClosedFloatingPointRange<Float> = 0f..360f
    override var leftLimit: ClosedFloatingPointRange<Float> = 0f..360f
    override var upLimit: ClosedFloatingPointRange<Float> = 0f..360f
    override val orientation = Orientation()
    override val forward get() = orientation.forward
    override val up get() = orientation.up
    override val leftOrRight get() = orientation.leftOrRight
    override val reversePerp get() = orientation.rightOrLeft

    override val position: Vector3
        get() {
            return if (parent != null) parent!!.position + localPosition else localPosition
        }
    override val allThings get() = toMap()

    override val children = mutableSetOf<IThing>()
    override var parent: IThing? = null
    override var rotateAroundUpEnabled: Boolean = true
    override var rotateAroundLeftEnabled: Boolean = true
    override var rotateAroundForwardEnabled: Boolean = true

    override fun addChild(child: IThing) {
        child.parent = this
        children.add(child)
    }

    override fun removeParent(child: IThing) {
        if (children.remove(child))
            child.parent = null
    }

    override fun rotate(aroundUp: Float, left: Float, aroundForward: Float) {
        val q = orientation.rotate(
            if (rotateAroundUpEnabled) aroundUp else 0f,
            if (rotateAroundLeftEnabled) left else 0f,
            if (rotateAroundForwardEnabled) aroundForward else 0f)

        for (child in children.asSequence().selectRecursive { children.asSequence() }.toList()) {
            q.transform(child.localPosition)
            q.transform(child.forward)
            q.transform(child.up)
        }
    }
}