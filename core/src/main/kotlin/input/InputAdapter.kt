package input

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.app.KtxInputAdapter
import ktx.ashley.hasNot
import ktx.ashley.mapperFor

class InputAdapter(
    private val controllers: MutableSet<Controller>,
    private var currentControlMapper: ControlMapper = inject()
) :
    KtxInputAdapter, ControllerListener {

    private val player: Player by lazy { inject() }
    private val currentController by lazy { controllers.first() }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> currentControlMapper.thrust = 1f
            Input.Keys.S -> currentControlMapper.thrust = -1f
            Input.Keys.A -> currentControlMapper.turning = -1f
            Input.Keys.D -> currentControlMapper.turning = 1f
            Input.Keys.SPACE -> currentControlMapper.firing = true
            else -> return false
        }
        return true
    }

    private fun toggleVehicle() {
        if (player.entity.hasNot(mapperFor<IsInVehicleComponent>())) {
            player.entity.add(EnterVehicleComponent())
        } else {
            player.entity.add(LeaveVehicleComponent())
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> currentControlMapper.thrust = 0f
            Input.Keys.S -> currentControlMapper.thrust = 0f
            Input.Keys.A -> currentControlMapper.turning = 0f
            Input.Keys.D -> currentControlMapper.turning = 0f
            Input.Keys.J -> toggleVehicle()
            Input.Keys.SPACE -> currentControlMapper.firing = false
            else -> return false
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if (button == Input.Buttons.LEFT) {
            currentControlMapper.firing = true
            true
        } else false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (!currentControlMapper.firing) {
            currentControlMapper.firing = true
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return if (button == Input.Buttons.LEFT) {
            currentControlMapper.firing = false
            true
        } else false
    }

    override fun connected(controller: Controller) {
        controllers.add(controller)
    }

    override fun disconnected(controller: Controller) {
        controllers.remove(controller)
    }

    override fun buttonDown(controller: Controller, buttonCode: Int): Boolean {
        return true
    }

    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        when(buttonMap[buttonCode]) {
            is Button.Green -> currentControlMapper.useGamePad = true
            is Button.Red -> currentControlMapper.useGamePad = false
            is Button.Blue -> {}
            is Button.Yellow -> {}
        }
        return true
    }
    private val buttonMap = mapOf(
        0 to Button.Green,
        1 to Button.Red,
        2 to Button.Blue,
        3 to Button.Yellow
    )

    private val axisMap = mapOf(
        5 to Axis.TriggerPull,
        0 to Axis.LeftX,
        1 to Axis.LeftY,
        2 to Axis.RightX,
        3 to Axis.RightY
    )

    override fun axisMoved(controller: Controller, axisCode: Int, value: Float): Boolean {
        if (controller == currentController) {
            when (axisMap[axisCode]) {
                is Axis.TriggerPull -> currentControlMapper.firing = value > 0.1f
                is Axis.LeftX -> currentControlMapper.turning = value
                is Axis.LeftY -> currentControlMapper.thrust = -value
                is Axis.RightX -> currentControlMapper.aimVector.set(value, currentControlMapper.aimVector.y)
                is Axis.RightY -> currentControlMapper.aimVector.set(currentControlMapper.aimVector.x, value)
            }
        }
        return true
    }
}

sealed class Button {
    object Green : Button()
    object Red : Button()
    object Blue : Button()
    object Yellow : Button()
}

sealed class Axis {
    object TriggerPull : Axis()
    object LeftX : Axis()
    object LeftY : Axis()
    object RightX : Axis()
    object RightY : Axis()
}