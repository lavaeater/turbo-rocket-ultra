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

    var aroundUpAngle: Float = 0f
    var uMin = 0f
    var uMax = 360f
    var aroundLeftAngle: Float = 0f
    var lMin = 0f
    var lMax = 360f
    var aroundForwardAngle: Float = 0f
    var fMin = 0f
    var fMax = 360f
    fun setUp(aroundUp: Float): Quaternion {
        return if((uMin..uMax).contains(aroundUpAngle + aroundUp)) {
            aroundUpAngle += aroundUp
            Quaternion(up, aroundUp)
        } else {
            if(aroundUp < 0f) {
                val newRot = uMin - aroundUpAngle
                aroundUpAngle = uMin
                Quaternion(up, newRot)
            } else {
                val newRot = uMax - aroundUpAngle
                aroundUpAngle = uMax
                Quaternion(up, newRot)
            }
        }
    }

    fun setForward(aroundForward: Float): Quaternion {
        return if((fMin..fMax).contains(aroundForwardAngle + aroundForward)) {
            aroundForwardAngle += aroundForward
            Quaternion(forward, aroundForward)
        } else {
            if(aroundForward < 0f) {
                val newRot = fMin - aroundForwardAngle
                aroundForwardAngle = fMin
                Quaternion(forward, newRot)
            } else {
                val newRot = fMax - aroundForwardAngle
                aroundForwardAngle = fMax
                Quaternion(forward, newRot)
            }
        }
    }

    fun setLeft(aroundLeft: Float): Quaternion {
        return if((lMin..lMax).contains(aroundLeftAngle + aroundLeft)) {
            aroundLeftAngle += aroundLeft
            Quaternion(leftOrRight, aroundLeft)
        } else {
            if(aroundLeft < 0f) {
                val newRot = lMin - aroundLeftAngle
                aroundLeftAngle = lMin
                Quaternion(leftOrRight, newRot)
            } else {
                val newRot = lMax - aroundLeftAngle
                aroundLeftAngle = lMax
                Quaternion(leftOrRight, newRot)
            }
        }
    }

    fun checkAngles() {
        rotate(uMin, lMin, fMin)
    }
    init {
        checkAngles()
    }

    fun rotate(aroundUp: Float, left: Float, aroundForward: Float): Quaternion {
        val q = setUp(aroundUp).mul(setLeft(left)).mul(setForward(aroundForward))
        q.transform(forward)
        q.transform(up)
        return q
    }
}