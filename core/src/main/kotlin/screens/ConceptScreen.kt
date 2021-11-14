package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.utils.viewport.ExtendViewport
import input.Transform
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.vec2
import ktx.math.vec3
import statemachine.StateMachine
import tru.Assets

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(200f, 200f, camera)
    val shapeDrawer by lazy { Assets.shapeDrawer }
    val transform: Transform = Transform(vec2(50f,50f))
    val detectables = listOf(Transform(vec2(72f,75f)))
    val aimSectorColor = Color(0f,0f,1f,0.2f)
    val rotationSectorColor = Color(0f,1f,0f,0.2f)
    var rotation = 45f


    override fun render(delta: Float) {
        super.render(delta)

        transform.rotate(rotation * delta)
4
        batch.use {
            shapeDrawer.pixelSize = 1f
            shapeDrawer.setColor(Color.RED)
            renderPosition(transform, Color.RED, Color.GREEN)
            renderAimAndMouse(transform)
            shapeDrawer.sector(
                transform.position.x,
                transform.position.y,
                100f,
                transform.aimVector.angleRad() - 45f * degreesToRadians,
                degreesToRadians * 90f, aimSectorColor, aimSectorColor)

            shapeDrawer.sector(
                transform.position.x,
                transform.position.y,
                100f,
                transform.forward.angleRad() - 45f * degreesToRadians,
                degreesToRadians * 90f, rotationSectorColor, rotationSectorColor)

            for(t in detectables) {
                renderPosition(t, Color.PURPLE, Color.CORAL)
            }
        }
    }

    fun renderAimAndMouse(transform: Transform) {
        shapeDrawer.setColor(Color.BLUE)
        shapeDrawer.line(transform.position, mousePosition)
        shapeDrawer.setColor(Color.RED)
        shapeDrawer.line(transform.position, transform.position.cpy().add(transform.aimVector.cpy().scl(10f)))

    }

    fun renderPosition(transform: Transform, positionColor: Color, indicatorColor: Color) {
        shapeDrawer.filledCircle(transform.position,1f, positionColor)
        shapeDrawer.setColor(indicatorColor)
        shapeDrawer.circle(transform.position.x, transform.position.y, 10f, 1f)
        shapeDrawer.line(transform.position, transform.forwardPoint)
    }

    val mousePosition3D = vec3()
    val mousePosition = vec2()

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition.set(mousePosition3D.x, mousePosition3D.y)
        transform.setAimVector(mousePosition)
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
        when(keycode) {
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