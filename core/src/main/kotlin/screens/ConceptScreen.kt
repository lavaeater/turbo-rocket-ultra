package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.vec2
import ktx.math.vec3
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val viewport = ExtendViewport(16f, 12f)

    var zoom = 0f
    var rotationY = 0f
    var rotationX = 0f
    var elapsedTime = 0f
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        super.render(delta)
        batch.use {
            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
        }
    }

    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 0.1f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -0.1f })
        setBoth(Input.Keys.A, "Rotate Left", { rotationY = 0f }) { rotationY = 5f }
        setBoth(Input.Keys.D, "Rotate Right", { rotationY = 0f }) { rotationY = -5f }
        setBoth(Input.Keys.W, "Rotate X", { rotationX = 0f }) { rotationX = 5f }
        setBoth(Input.Keys.S, "Rotate X", { rotationX = 0f }) { rotationX = -5f }
    }


    override fun keyUp(keycode: Int): Boolean {
        return normalCommandMap.execute(keycode, KeyPress.Up)
    }

    override fun keyDown(keycode: Int): Boolean {
        return normalCommandMap.execute(keycode, KeyPress.Down)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Buttons.LEFT) {
        }
        return true
    }

    val screenMouse = vec3()
    val mousePosition = vec2()
    fun updateMouse() {
        screenMouse.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(screenMouse)
        mousePosition.set(screenMouse.x, screenMouse.y)
    }
}
