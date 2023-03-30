package screens.concepts

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import input.Axis
import input.GamepadControl
import input.Transform
import ktx.graphics.use
import ktx.math.vec2
import ktx.math.vec3
import screens.BasicScreen
import statemachine.StateMachine
import tru.Assets

class ConceptScreenOld(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    /*
    Controllers exist by themselves. They might be connected or not connected
    This system we are making should handle all input, at least from controllers
    and how do we do that independent of entity systems etc?
     */
    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(200f, 200f, camera)
    val shapeDrawer by lazy { Assets.shapeDrawer }
    val player: Transform = Transform(vec2(100f, 100f))
    val mouseTransform = Transform(vec2())
    val detectables = listOf(mouseTransform)
    val detectedColor = Color(0f, 1f, 0f, 0.2f)
    val notDetectedColor = Color(0f, 0f, 1f, 0.2f)
    val fieldOfView = 270f

    var rotationSectorColor = notDetectedColor

    var rotation = 45f
    val viewDistance = 100f

    val controller: Controller by lazy { Controllers.getCurrent() }
    val gamepadControl by lazy { GamepadControl(controller) }


    override fun render(delta: Float) {
        super.render(delta)

        //player.rotate(rotation * delta)

        batch.use {
            shapeDrawer.pixelSize = 1f
            shapeDrawer.setColor(Color.RED)
            renderPosition(player, Color.RED, Color.GREEN)
            renderControllerAim(player, gamepadControl, delta)
//            renderAimAndMouse(player)

            var rotationDetected = false
            for (t in detectables) {
                if (player.dst(t) < viewDistance) {
                    rotationDetected = player.forwardAngleTo (t) < fieldOfView / 2
                }
                rotationSectorColor = if (rotationDetected) detectedColor else notDetectedColor
                renderPosition(t, Color.RED, Color.GREEN)
            }
            rotationSectorColor = notDetectedColor
            rotationSectorColor = if (rotationDetected) detectedColor else notDetectedColor

            shapeDrawer.sector(
                player.position.x,
                player.position.y,
                viewDistance,
                player.forward.angleRad() - fieldOfView / 2  * MathUtils.degreesToRadians,
                MathUtils.degreesToRadians * fieldOfView, rotationSectorColor, rotationSectorColor
            )
        }
    }

    val steeringMap = mapOf(
        2 to Axis.RightX,
        3 to Axis.RightY
    )
    val deadzone = -0.05f..0.05f
    val axisMap = steeringMap.map { it.value to it.key }.toMap()
    private val controllerVector = vec2()
    private fun renderControllerAim(player: Transform, gamepadControl: GamepadControl, deltaTime: Float) {
        val rightX = gamepadControl.controller.getAxis(axisMap[Axis.RightX]!!)
        val rightY = gamepadControl.controller.getAxis(axisMap[Axis.RightY]!!)
        controllerVector.x = if(deadzone.contains(rightX)) controllerVector.x else rightX
        controllerVector.y = if(deadzone.contains(rightY)) controllerVector.y else -rightY

        /**
         * Shit, this is fudging tricky.
         * How do we interpret the control input and the
         */

        player.aimVector.set(controllerVector).nor()
        Assets.font.draw(batch, "C: ${controllerVector.angleDeg()}", 10f,100f)
        Assets.font.draw(batch, "A: ${player.aimVector.angleDeg()}", 10f,80f)

        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(player.position, player.position.cpy().add(player.aimVector.cpy().scl(10f)))

        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(player.position, player.position.cpy().add(controllerVector.cpy().scl(10f)))
    }

    fun renderAimAndMouse(transform: Transform) {
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(transform.position, mousePosition)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(transform.position, transform.position.cpy().add(transform.aimVector.cpy().scl(10f)))

    }

    fun renderPosition(transform: Transform, positionColor: Color, indicatorColor: Color) {
        shapeDrawer.filledCircle(transform.position, 2f, positionColor)
        shapeDrawer.setColor(indicatorColor)
        shapeDrawer.circle(transform.position.x, transform.position.y, 10f, 1f)
        shapeDrawer.line(transform.position, transform.forwardPoint)
        shapeDrawer.line(transform.position, transform.normalPoint, Color.BLUE, 1f)
    }

    val mousePosition3D = vec3()
    val mousePosition = vec2()

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition.set(mousePosition3D.x, mousePosition3D.y)
//        player.pointAimVectorAt(mousePosition)
        mouseTransform.set(mousePosition)
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
//            Input.Keys.W -> keyboardControl.thrust = 0f
//            Input.Keys.S -> keyboardControl.thrust = 0f
            Input.Keys.A -> rotation = 90f
            Input.Keys.D -> rotation = -90f
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.A -> rotation = 0f
            Input.Keys.D -> rotation = 0f
        }
        return true
    }
}