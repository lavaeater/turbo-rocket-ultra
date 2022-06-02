package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import data.Players
import ecs.components.intent.IntendsTo
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import input.Axis
import input.Axis.Companion.valueOK
import input.Button
import input.GamepadControl
import input.entityFor
import ktx.ashley.allOf
import physics.getComponent
import physics.intendTo
import statemachine.StateMachine

/**
 * Controllers will be handled by a polling system
 */
class GamepadInputSystem : IteratingSystem(allOf(GamepadControl::class).get()), ControllerListener {
    private val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }

    private val inputActionHandler by lazy { inject<InputActionHandler>() }
    private val controllers: List<GamepadControl>
        get() = Players.players.keys.filterIsInstance<GamepadControl>().map { it }

    override fun connected(controller: Controller) {

    }

    override fun disconnected(controller: Controller?) {
    }

    override fun buttonDown(controller: Controller, buttonCode: Int): Boolean {
        val actualController = controllers.firstOrNull { it.controller == controller }
        if (actualController != null) {
            if (!actualController.requireSequencePress) {
                when (Button.getButton(buttonCode)) {
                    Button.Cross -> if (gameState.currentState.state == GameState.Paused) gameState.acceptEvent(
                        GameEvent.ResumedGame
                    ) else if (actualController.isInBuildMode) actualController.buildIfPossible =
                        true else actualController.doContextAction = true
                    Button.Options -> {
                        if (gameState.currentState.state == GameState.Running) gameState.acceptEvent(GameEvent.PausedGame) else if (gameState.currentState.state == GameState.Paused) gameState.acceptEvent(
                            GameEvent.ResumedGame
                        )
                    }
                    else -> {
                        if (gameState.currentState.state == GameState.Paused)
                            gameState.acceptEvent(GameEvent.ResumedGame)
                    }
                }
            }
        }
        return true
    }

    val hackingButtons = listOf(
        Button.getButtonCode(Button.DPadLeft),
        Button.getButtonCode(Button.DPadRight),
        Button.getButtonCode(Button.DPadUp),
        Button.getButtonCode(Button.DPadDown),
        Button.getButtonCode(Button.Ring)
    )

    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        val actualController = controllers.firstOrNull { it.controller == controller }
        if (actualController != null) {
            if (actualController.requireSequencePress && hackingButtons.contains(buttonCode)) {
                actualController.keyPressedCallback(buttonCode)
            } else {
                when (Button.getButton(buttonCode)) {
                    Button.Cross -> handleAction(actualController.entityFor())
                    Button.Ring -> {}
                    Button.Square -> actualController.entityFor()
                        .intendTo(IntendsTo.Reload)
                    Button.DPadLeft -> {
                        actualController.entityFor().intendTo(IntendsTo.SelectPreviousWeapon)
                    }
                    Button.DPadRight -> {
                        actualController.entityFor().intendTo(IntendsTo.SelectNextWeapon)
                    }
                    Button.Triangle -> toggleBuildMode(actualController.entityFor())

                    Button.DPadDown -> handleDown(actualController.entityFor())
                    Button.DPadUp -> handleUp(actualController.entityFor())
                    Button.L1 -> actualController.entityFor().intendTo(IntendsTo.SelectPreviousWeapon)

                    Button.L3 -> {}
                    Button.Options -> {}
                    Button.PsButton -> {}
                    Button.R1 -> actualController.entityFor().intendTo(IntendsTo.SelectNextWeapon)
                    Button.R3 -> {}
                    Button.Share -> {}
                    Button.Unknown -> {}
                }
            }
        }
        return true
    }

    private fun handleUp(entity: Entity) {
        inputActionHandler.previous(entity)
    }

    private fun handleDown(entity: Entity) {
        inputActionHandler.next(entity)
    }

    private fun handleAction(entity: Entity) {
        inputActionHandler.act(entity)
    }

    private fun toggleBuildMode(entity: Entity) {
        entity.intendTo(IntendsTo.ToggleBuildMode)
    }

    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        return true //Check if this actually works
    }

    private fun axisMoved(controller: GamepadControl, axisCode: Int, value: Float): Boolean {
        when (Axis.getAxis(axisCode)) {
            Axis.TriggerPull -> controller.firing = value > 0.3f
            Axis.LeftX -> if (valueOK(value)) controller.turning = value
            else controller.turning = 0f

            Axis.LeftY -> if (valueOK(value)) controller.thrust = -value
            else controller.thrust = 0f
//            Axis.RightX -> if (valueOK(value)) controller.aimVector.set(value, controller.aimVector.y).nor()
//            Axis.RightY -> if (valueOK(value)) controller.aimVector.set(controller.aimVector.x, value).nor()
            else -> {}
        }
        return true
    }

    private val steeringMap = mapOf(
        2 to Axis.RightX,
        3 to Axis.RightY
    )
    private val axisMap = steeringMap.map { it.value to it.key }.toMap()

    val deadZone = -0.2f..0.2f

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<GamepadControl>()

        val controller = controlComponent.controller
        for (axis in Axis.axisMap.keys) {
            axisMoved(controlComponent, axis, controller.getAxis(axis))
        }
        val rightX = controller.getAxis(axisMap[Axis.RightX]!!)
        val rightY = controller.getAxis(axisMap[Axis.RightY]!!)

        controlComponent.aimVector.set(rightX, rightY) //SHoult NOT normalize, gerddamnit
    }

}