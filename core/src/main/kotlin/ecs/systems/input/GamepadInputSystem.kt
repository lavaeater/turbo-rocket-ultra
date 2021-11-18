package ecs.systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.math.MathUtils
import data.Players
import input.Axis
import input.Axis.Companion.valueOK
import input.Button
import input.GamepadControl
import input.InputIndicator
import ktx.ashley.allOf
import ktx.math.vec2
import net.dermetfan.utils.math.MathUtils.between
import physics.getComponent

/**
 * Controllers will be handled by a polling system
 */
class GamepadInputSystem() : IteratingSystem(allOf(GamepadControl::class).get()), ControllerListener {
    private val controllers: List<GamepadControl>
        get() = Players.players.keys.filterIsInstance<GamepadControl>().map { it }

    override fun connected(controller: Controller) {

    }

    override fun disconnected(controller: Controller?) {
    }

    override fun buttonDown(controller: Controller, buttonCode: Int): Boolean {
        return true
    }

    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        val actualController = controllers.firstOrNull { it.controller == controller }
        if (actualController != null) {
            when (Button.getButton(buttonCode)) {
                Button.Cross -> actualController.doContextAction = true
                Button.Ring -> {}
                Button.Square -> actualController.needsReload = true
                Button.DPadLeft -> actualController.needToChangeGun = InputIndicator.Previous
                Button.DPadRight -> actualController.needToChangeGun = InputIndicator.Next
                Button.Triangle -> actualController.isBuilding = !actualController.isBuilding
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
            Axis.RightX -> if (valueOK(value)) controller.aimVector.set(value, controller.aimVector.y).nor()
            Axis.RightY -> if (valueOK(value)) controller.aimVector.set(controller.aimVector.x, value).nor()
            Axis.Unknown -> {
            }
        }
        return true
    }

    val steeringMap = mapOf(
        2 to Axis.RightX,
        3 to Axis.RightY
    )
    val axisMap = steeringMap.map { it.value to it.key }.toMap()

    val deadZone = -0.2f..0.2f

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<GamepadControl>()

        val controller = controlComponent.controller
        for (axis in Axis.axisMap.keys) {
            axisMoved(controlComponent, axis, controller.getAxis(axis))
        }
        var rightX = controller.getAxis(axisMap[Axis.RightX]!!)
        var rightY = controller.getAxis(axisMap[Axis.RightY]!!)

//        if (deadZone.contains(rightX))
//            rightX = 0f
//        if (deadZone.contains(rightY))
//            rightY = 0f

        controlComponent.aimVector.set(vec2(rightX, rightY)).nor()

        /*
        it should obviously work on axeses in PAIRS, like leftx + lefty and rightx and righty in  tandem, because then
        we can set the aimvector to the values, straight up, and normalize it.

        But that isn't completely correct either. The aimvector should be nudged to it's correct value, kinda
        THis needs testing in the CONCEPT SCREEN!
         */

    }

}