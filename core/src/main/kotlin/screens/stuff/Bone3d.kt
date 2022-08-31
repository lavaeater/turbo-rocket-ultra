package screens.stuff

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import data.selectedItemListOf
import ktx.math.plus

//    private val skeleton = Bone3d("body", vec3(), vec3(0f, 10f, 0f)).apply {
//        addChild(Bone3d("extremity", vec3(-2.5f, 0f, 0f), vec3(0f, 5f, 0f)))
//    }

//    private val skeleton = Bone3d("body", vec3(), vec3(0f, 10f, 0f)).apply {
//        addChild(Bone3d("left-arm-upper", vec3(-2.5f, 0f, 0f), vec3(0f, 5f, 0f)).apply {
//            addChild(Bone3d("left-arm-lower", vec3(0f, 0f, 0f), vec3(0f, 5f, 0f)))
//        })
//        addChild(Bone3d("right-arm-upper", vec3(2.5f, 0f, 0f), vec3(0f, 5f, 0f)).apply {
//            addChild(Bone3d("right-arm-lower", vec3(0f, 0f, 0f), vec3(0f, 5f, 0f)))
//        })
//        addChild(Bone3d("right-leg", vec3(3f, -10f, 0f), vec3(0f, -10f, 0f)))
//        addChild(Bone3d("left-leg", vec3(-3f, -10f, 0f), vec3(0f, -10f, 0f)))
//    }

//val boneList = selectedItemListOf(*skeleton.bones.values.toTypedArray())

open class Bone3d(
    var name: String,
    val localStart: Vector3,
    val boneVector: Vector3,
    var parent: Bone3d? = null,
    val updateActions: MutableList<(Bone3d, Float) -> Unit> = mutableListOf()
) {
    val bones: Map<String, Bone3d> get() = toMap()

    val globalStart: Vector3
        get() {
            return if (parent == null) localStart else parent!!.globalEnd + localStart
        }
    val globalEnd: Vector3
        get() {
            return globalStart + boneVector
        }

    private val _children = mutableSetOf<Bone3d>()
    val children: Set<Bone3d>
        get() = _children

    fun addChild(child: Bone3d): Bone3d {
        _children.add(child)
        child.parent = this
        return child
    }

    fun removeChild(child: Bone3d): Bone3d {
        _children.remove(child)
        return child
    }

    fun update(delta: Float) {
        updateActions.forEach { it(this, delta) }
        children.forEach { it.update(delta) }
    }

    fun eulerian(yaw: Float, pitch: Float, roll: Float) {


        val yawning = Quaternion().setEulerAngles(yaw, 0f, 0f)
        val pitching = Quaternion().setEulerAngles(0f, pitch, 0f)
        val rolling = Quaternion().setEulerAngles(0f, 0f, roll)

        val result = Quaternion(yawning).mul(pitching).mul(rolling)




        result.transform(localStart)
        result.transform(boneVector)
        for (child in children)
            child.eulerian(yaw, pitch, roll)
    }

    fun rotateAround(xAxis: Float, yAxis: Float, zAxis: Float) {
        /**
         * I think local start shouldn't rotate, but boneVector should? Experiment!
         *
         * This is global rotation, used for rotating the entire skeleton
         */
        val q = Quaternion(Vector3.X, xAxis).mul(Quaternion(Vector3.Y, yAxis)).mul(Quaternion(Vector3.Z, zAxis))
        q.transform(localStart)
        q.transform(boneVector)
        for(child in children)
            child.rotateAround(xAxis, yAxis, zAxis)
    }

    fun rotateBy(x: Float, y: Float, z: Float) {
        /*
        To make it easy, to test, we are doing TWO rotations only

        1st is around the axis that is perpendicular to the plane created
        by this bone and the parent bone.

        The 2nd is around the axis that is the parent bone.

        The third would be roll, rotation around... itself?

        Start with two, because that last one... is hard to grasp.

        Rotations mean different things if we are with a parent or without a parent.

        Ignore Z, man
         */

        if(parent == null) {
            /*
            We will rotate everything on unit axes
             */
            val q = Quaternion(Vector3.Z, x).mul(Quaternion(Vector3.Y, y))//.mul(Quaternion(Vector3.Z, z))
            q.transform(localStart)
            q.transform(boneVector)
        } else {
            /**
             * This is almost correct.
             * These things are different concepts.
             *
             * One is "bend a joint", basically.
             *
             * The other is "orient the entire bone a certain way in relation to other things.
             *
             * We must be able to do both things.
             *
             * So, in the case of rotating the body, we want to do that universally so just all points have
             * the absolutely correct positions afterwards - this is most like different from the local
             * rotation requirement. I, for instance, have no clear idea on how to rotate around Z in that case.
             *
             *
             *
             */
            val xRotationAxis = parent!!.boneVector.cpy().nor()
            val yRotationAxis = boneVector.cpy().crs(parent!!.boneVector).nor()

            val q = Quaternion(xRotationAxis, x).mul(Quaternion(yRotationAxis, y))
            q.transform(localStart)
            q.transform(boneVector)
        }
    }
}

fun Bone3d.toMap(): Map<String, Bone3d> {
    return listOf(
        this,
        *children.asSequence().selectRecursive { children.asSequence() }.toList().toTypedArray()
    ).associateBy { it.name }
}