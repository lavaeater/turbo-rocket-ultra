package screens

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import screens.stuff.AnimatedSprited3d

interface IThing {
    var forwardLimit: ClosedFloatingPointRange<Float>
    var leftLimit: ClosedFloatingPointRange<Float>
    var upLimit: ClosedFloatingPointRange<Float>
    var name: String
    val localPosition: Vector3
    val orientation: Orientation
    val forward: Vector3
    val up: Vector3
    val leftOrRight: Vector3
    val reversePerp: Vector3
    val position: Vector3
    val allThings: Map<String, IThing>
    val children: MutableSet<IThing>
    var parent: IThing?
    var rotateAroundUpEnabled: Boolean
    var rotateAroundLeftEnabled: Boolean
    var rotateAroundForwardEnabled: Boolean
    val attachments: MutableList<AnimatedSprited3d>
    fun addChild(child: IThing)
    fun removeParent(child: IThing)
    fun rotate(aroundUp: Float, left: Float, aroundForward: Float)

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

    fun angleToParent(): Float

    /**
     * We always calculate this against up, I guess?
     *
     * This is clearly some angle around forward, of course. Hmm.
     */
    fun rotateAroundParent(degrees: Float)
    fun rotateAgainstJoint(degrees: Float)
    fun rotateAroundSelf(degrees: Float)
    fun rotate(q: Quaternion)
}