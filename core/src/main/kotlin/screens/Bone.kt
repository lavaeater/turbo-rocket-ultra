package screens

import com.badlogic.gdx.math.Vector3
import ktx.math.vec3

open class Bone(name: String, localPosition: Vector3, val boneLength: Float) : Thing(name, localPosition) {
    var boneEnd: Vector3 = vec3()
        private set
        get() {
            field.set(orientation.forward.cpy().scl(boneLength).add(position))
            return field
        }
}