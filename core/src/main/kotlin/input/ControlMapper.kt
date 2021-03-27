package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import injection.Context.inject
import ktx.math.vec2
import ktx.math.vec3

interface ControlMapper: Component {

    val isKeyboard: Boolean
    val isGamepad: Boolean
    val aimVector: Vector2
    val mousePosition: Vector2
    var firing: Boolean
    var turning: Float
    var thrust: Float

    val walkVector: Vector2

    fun setAimVector(screenX: Int, screenY: Int, position: Vector2)



}

class KeyboardControl: ControlMapper, Component {
    private val camera by lazy { inject<OrthographicCamera>() }
    private val mousePosition3D = vec3()
    override val isKeyboard = true
    override val isGamepad = false
    override val aimVector = vec2()
    override val mousePosition = vec2()
    override var firing = false
    override var turning = 0f
    override var thrust = 0f
    override val walkVector: Vector2 = vec2(turning, thrust)
        get() = field.set(turning, -thrust)


    override fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition.set(mousePosition3D.x, mousePosition3D.y)
        aimVector.set(mousePosition).sub(position).nor()
    }
}

class GamepadControl(val controller: Controller): ControlMapper, Component {
    override val aimVector = vec2()
    override val mousePosition = vec2()
    override var firing = false
    override var turning = 0f
    override var thrust = 0f
    override val isKeyboard = false
    override val isGamepad = true
    override val walkVector: Vector2 = vec2(turning, thrust)
        get() = field.set(turning, -thrust)

    override fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {
        //no-op because this is a gamepad, mate.
    }

}

