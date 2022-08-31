package screens

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import ktx.math.unaryMinus
import ktx.math.vec3

open class Orientation {

    val forward = vec3(0f, 0f, -1f) //towards screen
    val up = vec3(0f, 1f, 0f)
    val leftOrRight: Vector3
        get() {
            return forward.cpy().crs(up).nor()
        }
    val rightOrLeft: Vector3
        get() {
            return -leftOrRight
        }

    fun rotate(aroundUp: Float, left: Float, aroundForward: Float): Quaternion {
        val q = Quaternion(up, aroundUp).mul(Quaternion(leftOrRight, left)).mul(Quaternion(forward, aroundForward))
        q.transform(forward)
        q.transform(up)
        return q
    }
}