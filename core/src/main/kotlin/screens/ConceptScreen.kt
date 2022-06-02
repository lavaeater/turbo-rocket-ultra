package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.systems.graphics.GameConstants.GAME_HEIGHT
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import ktx.actors.centerPosition
import ktx.actors.then
import ktx.math.vec2
import ktx.math.vec3
import ktx.scene2d.dialog
import ktx.scene2d.label
import ktx.scene2d.scene2d
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

    const val margin = 2.5f
    const val minX = 0f + margin
    const val maxX = GAME_HEIGHT - margin
    const val maxY = GAME_HEIGHT - margin
    const val minY = 0f + margin
}


fun Vector2.worldToNorm(): Vector2 {
    return this.worldToNorm(MousePosition.minX, MousePosition.maxX, MousePosition.minY, MousePosition.maxY)
}

fun Vector2.worldToNorm(minX: Float, maxX: Float, minY: Float, maxY: Float): Vector2 {
    return vec2(MathUtils.norm(minX, maxX, x), MathUtils.norm(minY, maxY, y))
}

fun Vector2.normToWorld(): Vector2 {
    return normToWorld(MousePosition.minX, MousePosition.maxX, MousePosition.minY, MousePosition.maxY)
}

fun Vector2.normToWorld(minX: Float, maxX: Float, minY: Float, maxY: Float): Vector2 {
    return vec2(MathUtils.lerp(minX, maxX, x), MathUtils.lerp(minY, maxY, y))
}


class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private var drawPointerBall = false
    private val normalCommandMap = command("Normal") {
        setUp(Input.Keys.SPACE, "Show the scrolling dialog") {
            CrawlDialog.showDialog(
                crawlDialog,
                """
            This is it.
            The scrolling pane,
            the pane of scrolls.T
            It goes from bottom to up, 
            in a most satisifying
            temperament
        """.trimIndent()
            )
        }
    }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    private val dialogHeight = 200f
    private val duration = 10f


    private val stage by lazy {
        val aStage = ktx.actors.stage(batch, ExtendViewport(800f, 600f, OrthographicCamera()))
        aStage
    }

    private val crawlDialog by lazy {
        val dialog = scene2d.dialog("Test of Scroll in dialog") {
        }
        stage.addActor(dialog)
        dialog
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
        return true
    }

    override fun show() {
        super.show()
    }

    override fun render(delta: Float) {
        super.render(delta)
        stage.act()
        stage.draw()
    }
}

object CrawlDialog {
    private val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }
    fun getLabelsFromString(text: String, dialogHeight: Float, duration: Float, autoQuit: Boolean): MutableList<Label> {
        val lines = text.lines()
        return lines.mapIndexed { index, line ->
            scene2d.label(line) {
                if (autoQuit && index == lines.lastIndex) {
                    addAction(
                        Actions.moveBy(0f, dialogHeight, duration, Interpolation.linear).then(
                            object : Action() {
                                override fun act(delta: Float): Boolean {
                                    if (gameState.currentState.state == GameState.Paused)
                                        gameState.acceptEvent(GameEvent.ResumedGame)
                                    return true
                                }
                            }).then(Actions.removeActor())
                    )
                } else
                    addAction(
                        Actions.moveBy(0f, dialogHeight, duration, Interpolation.linear).then(Actions.removeActor())
                    )
            }
        }.toMutableList()
    }

    fun showDialog(
        dialog: Dialog,
        text: String,
        dialogHeight: Float = 200f,
        duration: Float = 10f,
        autoQuit: Boolean = true
    ) {
        val linesToScroll = getLabelsFromString(text, dialogHeight, duration, autoQuit)
        val stage = dialog.stage
        val textHeight = linesToScroll.maxOf { it.height }
        val textWidth = linesToScroll.maxOf { it.width } * 1.5f

        dialog.show(stage, Actions.repeat(linesToScroll.size, Actions.delay(1f, object : Action() {
            override fun act(delta: Float): Boolean {
                val l = linesToScroll.removeFirst()
                stage.addActor(l)
                l.setPosition(
                    stage.width / 2 - (l.width / 2f),
                    stage.height / 2 - dialog.height / 2 + textHeight
                )
                return true
            }
        })))
        dialog.apply {
            contentTable.setFillParent(true)
            width = textWidth
            height = dialogHeight * 1.25f
            //delay can be calculated from label heights.
            centerPosition()
        }
    }
}

