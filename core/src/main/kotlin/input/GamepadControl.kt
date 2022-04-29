package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.Vector2
import ecs.components.player.PlayerMode
import ktx.math.vec2

class GamepadControl(val controller: Controller): ControlMapper, Component {
    override var needsReload = false
    override var doContextAction = false
    override val aimVector = vec2()
    override var aiming: Boolean get() = aimVector.len2() > .3f
        set(value) {}
    override val mousePosition = vec2()
    override var firing = false
    override var turning = 0f
    override var thrust = 0f
    override var needToChangeGun: InputIndicator = InputIndicator.Neutral
    override val isKeyboard = false
    override val isGamepad = true
    override val walkVector: Vector2 = vec2(turning, thrust)
        get() = field.set(turning, -thrust)
    override val controllerId: String get() = controller.toString()
    override var playerMode: PlayerMode = PlayerMode.Control

    override fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {
        //no-op because this is a gamepad, mate.
    }

    override var uiControl: UserInterfaceControl = NoOpUserInterfaceControl.control
}