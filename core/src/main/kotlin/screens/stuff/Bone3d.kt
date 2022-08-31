package screens.stuff

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import ktx.math.plus

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

    fun rotateBy(degrees: Float, axis: Vector3) {
        val localNormalized = localStart.cpy().nor()

        val quaternion = Quaternion(axis, degrees)
        val transform = Matrix4(quaternion)
        localStart.rot(transform)
        boneVector.rot(transform)
        for (child in children)
            child.rotateBy(degrees, axis)
    }
}

fun Bone3d.toMap(): Map<String, Bone3d> {
    return listOf(
        this,
        *children.asSequence().selectRecursive { children.asSequence() }.toList().toTypedArray()
    ).associateBy { it.name }
}