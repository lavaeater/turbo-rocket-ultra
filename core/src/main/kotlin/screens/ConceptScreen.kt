package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
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
    override val viewport = ExtendViewport(100f, 100f, camera)
    val shapeDrawer by lazy { Assets.shapeDrawer }
    val transform: Transform = Transform(vec2(50f,50f))

    override fun render(delta: Float) {
        super.render(delta)

        transform.rotate(rotation * delta)

        batch.use {
            shapeDrawer.pixelSize = 1f
            shapeDrawer.setColor(Color.RED)
            shapeDrawer.filledCircle(transform.position,1f)
            shapeDrawer.setColor(Color.GREEN)
            shapeDrawer.circle(transform.position.x, transform.position.y, 10f, 1f)
            val end = transform.forwardAt(10f)
            shapeDrawer.line(transform.position, end)
            shapeDrawer.setColor(Color.BLUE)
            shapeDrawer.line(transform.position, mousePosition)
            shapeDrawer.setColor(Color.RED)
            end.set(transform.position).add(transform.aimVector.scl(10f))
            shapeDrawer.line(transform.position, end)
        }
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

    var rotation = 0f

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