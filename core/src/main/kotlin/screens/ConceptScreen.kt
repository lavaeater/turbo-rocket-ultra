package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import ktx.graphics.use
import ktx.math.vec2
import ktx.math.vec3
import statemachine.StateMachine
import tru.Assets

object MousePosition {
    private val mousePosition3D = vec3()
    private val mousePosition2D = vec2()
    private val camera by lazy { inject<OrthographicCamera>() }
    fun toWorld(screenX: Int, screenY: Int): Vector2 {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition2D.set(mousePosition3D.x, mousePosition3D.y)
        return mousePosition2D
    }

    fun toWorld(): Vector2 {
        return toWorld(Gdx.input.x, Gdx.input.y)
    }
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private var drawPointerBall = false
    private val normalCommandMap = command("Normal") {
    }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    private val stage by lazy {
        val aStage = Stage(ExtendViewport(1600f, 1200f, OrthographicCamera()), batch)
    }

    private val points = mutableListOf<Vector2>()

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        drawPointerBall = button == Buttons.LEFT
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        drawPointerBall = false
        if (button == Buttons.LEFT)
            addPointAt(screenX, screenY)
        return true
    }

    private fun addPointAt(screenX: Int, screenY: Int) {
        val cp = MousePosition.toWorld(screenX, screenY).cpy().nor()
        val p = MousePosition.toWorld(screenX, screenY).cpy()
            .apply {
                x = MathUtils.norm(leftX, rightX, x)
                y = MathUtils.norm(bottomY, topY, y)
            }
        points.add(p) // We need to project this sucker into -1..1 coords nor to the rescue
    }

    private val leftX by lazy { camera.position.x - viewport.worldWidth / 2 }
    private val rightX by lazy { camera.position.x + viewport.worldWidth / 2 }
    private val topY by lazy { camera.position.y + viewport.worldHeight / 2 }
    private val bottomY by lazy { camera.position.y - viewport.worldHeight / 2 }
    private val drawPosition = vec2()

    override fun render(delta: Float) {
        super.render(delta)
        shapeDrawer.batch.use {
            if (drawPointerBall) {
                shapeDrawer.filledCircle(MousePosition.toWorld(), .5f, Color.GREEN)
            }
            for (point in points) {
                drawPosition.set(MathUtils.lerp(leftX, rightX, point.x), MathUtils.lerp(bottomY, topY, point.y))
                shapeDrawer.filledCircle(
                    drawPosition,
                    .5f,
                    Color.RED
                )
            }
        }
    }
}