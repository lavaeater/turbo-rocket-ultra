package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import input.Axis
import input.Axis.Companion.valueOK
import input.Button
import input.GamepadControl
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import gamestate.Players

/**
 * Controllers will be handled by a polling system
 */
class GamepadInputSystem() : IteratingSystem(allOf(GamepadControl::class).get()), ControllerListener {

    private val gcMapper = mapperFor<GamepadControl>()
    private val controllers: List<GamepadControl> get() = Players.players.values.filterIsInstance<GamepadControl>().map { it }
    override fun connected(controller: Controller) {

    }

    override fun disconnected(controller: Controller?) {
    }

    override fun buttonDown(controller: Controller, buttonCode: Int): Boolean {
        return true
    }

    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        val actualController = controllers.firstOrNull { it.controller == controller }
        if(actualController != null) {
            when (Button.getButton(buttonCode)) {
                Button.Green -> {}
                Button.Red -> {}
                Button.Blue -> {}
                Button.Yellow -> {}
                Button.Unknown -> {}
            }
        }
        return true
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

            Axis.RightX -> if (valueOK(value)) controller.aimVector.set(
                MathUtils.lerp(controller.aimVector.x, value, 0.1f),
                controller.aimVector.y)

            Axis.RightY -> if (valueOK(value)) controller.aimVector.set(
                controller.aimVector.x,
                MathUtils.lerp(controller.aimVector.y, value, 0.1f)
            )
            Axis.Unknown -> {}
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

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = gcMapper[entity]
        val controller = controlComponent.controller
        for(axis in Axis.axisMap.keys) {
            axisMoved(controlComponent, axis, controller.getAxis(axis))
        }
    }

}