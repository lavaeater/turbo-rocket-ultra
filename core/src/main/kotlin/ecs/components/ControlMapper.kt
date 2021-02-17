package ecs.components

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class ControlMapper {
    val mousePosition = vec2(0f, 0f)

    val aimVector: Vector2 = vec2(0f,0f)

    var firing: Boolean = false
    var turning: Float = 0f
    var thrust: Float = 0f
}

