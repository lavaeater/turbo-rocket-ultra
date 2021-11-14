package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import input.Transform
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.minus
import ktx.math.vec2
import ktx.math.vec3
import statemachine.StateMachine
import tru.Assets

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
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


    override fun render(delta: Float) {
        super.render(delta)

        player.rotate(rotation * delta)
        4
        batch.use {
            shapeDrawer.pixelSize = 1f
            shapeDrawer.setColor(Color.RED)
            renderPosition(player, Color.RED, Color.GREEN)
            renderAimAndMouse(player)

            var rotationDetected = false
            for (t in detectables) {
                if (player.dst(t) < viewDistance) {
                    rotationDetected = player.angleTo (t) < fieldOfView / 2
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
                player.forward.angleRad() - fieldOfView / 2  * degreesToRadians,
                degreesToRadians * fieldOfView, rotationSectorColor, rotationSectorColor
            )
        }
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
        player.setAimVector(mousePosition)
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

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false)
        viewport.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }
}

fun Transform.angleTo(other: Transform) : Float {
    return acos(this.forward.dot(other.position.cpy().sub(this.position).nor())) * radiansToDegrees
}

/***
 * Returns angle in degrees to @param positionVector
 */
fun Vector2.angleTo(positionVector: Vector2): Float {
    return (acos(this.dot(this.cpy().sub(positionVector).nor()))) * radiansToDegrees
}