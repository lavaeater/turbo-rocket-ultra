package screens

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import ktx.math.plus
import screens.stuff.AnimatedSprited3d
import screens.stuff.selectRecursive

open class Thing(override var name: String, override val localPosition: Vector3) : IThing {
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

    override val attachments = mutableListOf<AnimatedSprited3d>()
    override fun addChild(child: IThing) {
        child.parent = this
        children.add(child)
    }

    override fun removeParent(child: IThing) {
        if (children.remove(child))
            child.parent = null
    }

    override fun rotate(aroundUp: Float, left: Float, aroundForward: Float) {
        val q = orientation.getQuaternion(
            if (rotateAroundUpEnabled) aroundUp else 0f,
            if (rotateAroundLeftEnabled) left else 0f,
            if (rotateAroundForwardEnabled) aroundForward else 0f)

        rotate(q)
    }

    /**
     * We shall constrain the bone against it's parent bone. That might actually work.
     *
     * So how?
     *
     * When we have a bone, all our rotations should be in relation to the parent bone, right?
     *
     * So, we have one rotation that will be against the plane. We then have to figure out what the angle between
     * those two vectors are, in the plane of the vectors.
     *
     * And in fact, we can ONLY rotate a bone around either that vector itself OR the perpendicular vector.
     *
     * Explore!
     *
     * Angle between is dot product of vectors. If they are always the forward vector, then that is kinda easy
     *
     */

    override fun angleToParent(): Float {
        return if(parent == null) 0f
        else {
            MathUtils.acos(parent!!.orientation.forward.cpy().dot(orientation.forward))
        }
    }

    /**
     * We always calculate this against up, I guess?
     *
     * This is clearly some angle around forward, of course. Hmm.
     */
    override fun rotateAroundParent(degrees: Float) {
        if(parent == null) {
            rotate(degrees, 0f, 0f)
            return
        }
        val pO = parent!!.orientation
        val q = Quaternion(pO.forward, degrees)
        rotate(q)
    }

    override fun rotateAroundSelf(degrees: Float) {
        rotate(0f,0f, degrees)
    }

    override fun rotateAgainstJoint(degrees: Float) {
        if(parent == null) {
            rotate(0f, degrees, 0f)
            return
        }
//        val pO = parent!!.orientation
//        val q = Quaternion(forward.cpy().crs(pO.forward), degrees)
        rotate(0f, degrees, 0f)
    }

    override fun rotate(q: Quaternion) {
        q.transform(orientation.forward)
        q.transform(orientation.up)

//        for(a in attachments) {
//            q.transform(a.position3d)
//        }

        for (child in children.asSequence().selectRecursive { children.asSequence() }.toList()) {
            q.transform(child.localPosition)
            q.transform(child.forward)
            q.transform(child.up)
//            for(a in child.attachments)
//                q.transform(a.position3d)
        }
    }
}