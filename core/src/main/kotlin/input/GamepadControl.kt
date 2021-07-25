package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.Vector2
import ecs.components.player.PlayerMode
import ktx.math.vec2
import uk.co.electronstudio.sdl2gdx.SDL2Controller

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
    override val controllerId: String
        get() {
            return if(controller is SDL2Controller)
                "Gamepad ${controller.playerIndex + 1}"
            else
                controller.toString()
        }
    override var playerMode: PlayerMode = PlayerMode.Control

    override fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {
        //no-op because this is a gamepad, mate.
    }

    override var uiControl: UserInterfaceControl = NoOpUserInterfaceControl.control
}