package input

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.app.KtxInputAdapter
import ktx.ashley.hasNot
import ktx.ashley.mapperFor


class InputHandlerThingie(
    private val controllers: MutableSet<Controller> = mutableSetOf(),
    private var currentControlMapper: ControlMapper = inject()
) :
    KtxInputAdapter, ControllerListener {

    private val player: Player by lazy { inject() }
//    private val currentController by lazy { controllers.first() }

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
        when (buttonMap[buttonCode]) {
            is Button.Green -> currentControlMapper.useGamePad = true
            is Button.Red -> currentControlMapper.useGamePad = false
            is Button.Blue -> {
            }
            is Button.Yellow -> {
            }
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
        when (axisMap[axisCode]) {
            is Axis.TriggerPull -> currentControlMapper.firing = value > 0.3f
            is Axis.LeftX -> if (valueOK(value)) currentControlMapper.turning =
                value else currentControlMapper.turning = 0f
            is Axis.LeftY -> if (valueOK(value)) currentControlMapper.thrust = -value else currentControlMapper.thrust =
                0f

            is Axis.RightX -> if (valueOK(value)) currentControlMapper.aimVector.set(
                MathUtils.lerp(currentControlMapper.aimVector.x, value, 0.1f),
                currentControlMapper.aimVector.y
            )
            is Axis.RightY -> if (valueOK(value)) currentControlMapper.aimVector.set(
                currentControlMapper.aimVector.x,
                MathUtils.lerp(currentControlMapper.aimVector.y, value, 0.1f)
            )
        }
        return true
    }

    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        return true
    }

    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        return true
    }

    fun valueOK(value: Float): Boolean {
        return value > 0.3f || value < -0.3f
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