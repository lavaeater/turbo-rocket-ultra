package ecs.components

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import injection.Context.inject
import ktx.math.vec2
import ktx.math.vec3

class ControlMapper {
    private val mousePosition3D = vec3()
    val mousePosition = vec2(0f, 0f)
    private val camera by lazy { inject<OrthographicCamera>() }

    val aimVector: Vector2 = vec2(0f,0f)

    var firing: Boolean = false
    var turning: Float = 0f
    var thrust: Float = 0f

    var useGamePad = true

    val walkVector: Vector2 = vec2(0f, 0f)
        get() = field.set(turning, -thrust)

    fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition.set(mousePosition3D.x, mousePosition3D.y)
        if(!useGamePad)
            aimVector.set(mousePosition).sub(position).nor()
    }

}

