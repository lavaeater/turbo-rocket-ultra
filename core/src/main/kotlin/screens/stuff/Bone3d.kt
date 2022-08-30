package screens.stuff

import com.badlogic.gdx.math.Vector3
import ktx.math.plus

open class Bone3d(
    var name: String,
    val localStart: Vector3,
    val boneVector: Vector3,
    var parent: Bone3d? = null,
    val updateActions: MutableList<(Bone3d, Float) -> Unit> = mutableListOf()
) {
    val globalStart: Vector3
        get() {
            return if(parent == null) localStart else parent!!.globalEnd + localStart
        }
    val globalEnd: Vector3
        get() {
            return globalStart + boneVector
        }

    val children = mutableListOf<Bone3d>()

    fun update(delta: Float) {
        updateActions.forEach { it(this, delta) }
        children.forEach { it.update(delta) }
    }
}