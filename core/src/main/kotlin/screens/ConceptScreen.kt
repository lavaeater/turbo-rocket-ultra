package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.scene2d.actors
import ktx.scene2d.dialog
import ktx.scene2d.label
import ktx.scene2d.scene2d
import statemachine.StateMachine
import tru.Assets
import ui.CrawlDialog
import kotlin.reflect.full.starProjectedType

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private var drawPointerBall = false
    private val valueSize = 10
    private val values = Array(valueSize) {
        (it * 1f / valueSize.toFloat()).toFloat()
    }
    private val interpolations = mapOf(
        "Exp 10    " to Interpolation.exp10,
        "Exp 10 In " to Interpolation.exp10In,
        "Exp 10 Out" to Interpolation.exp10Out,
        "Exp 5     " to Interpolation.exp5,
        "Exp 5 In  " to Interpolation.exp5In,
        "Exp 5 In  " to Interpolation.exp5Out,
//    Interpolation.fastSlow,
//        Interpolation.pow2,
//        Interpolation.pow2In,
//        Interpolation.pow2Out,
//        Interpolation.pow4,
//        Interpolation.pow4In,
//    Interpolation.pow4Out,
//        Interpolation.slowFast
    )

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
        aStage.actors {
            label(interpolations.keys.joinToString("\n") { ip -> ip + values.joinToString { "${(interpolations[ip]!!.apply(it)* 100).toInt()}" }}).apply {
                setFontScale(0.5f)
            }
        }
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

