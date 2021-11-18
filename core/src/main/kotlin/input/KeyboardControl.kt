package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ecs.components.player.PlayerMode
import injection.Context
import ktx.math.vec2
import ktx.math.vec3

class KeyboardControl: ControlMapper, Component {
    private val camera by lazy { Context.inject<OrthographicCamera>() }
    private val mousePosition3D = vec3()
    override var needsReload = false
    override val isKeyboard = true
    override val isGamepad = false
    override val aimVector = vec2(1f,0f) //always have length 1
    override var aiming: Boolean = false
    override val mousePosition = vec2()
    override var firing = false
    override var turning = 0f
    override var thrust = 0f
    override var needToChangeGun: InputIndicator = InputIndicator.Neutral
    override var doContextAction: Boolean = false
    override val walkVector: Vector2 = vec2(turning, thrust)
        get() = field.set(turning, -thrust)
    override val controllerId: String
        get() = "Keyboard and mouse"

    override var isBuilding: Boolean = false

    override fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition.set(mousePosition3D.x, mousePosition3D.y)
        aimVector.set(mousePosition).sub(position).nor()
    }

    override var uiControl: UserInterfaceControl = NoOpUserInterfaceControl.control
}