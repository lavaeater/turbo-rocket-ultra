package screens.concept

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
import screens.basic.BasicScreen
import screens.command.command
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets
import ui.CrawlDialog

class HardPointConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    private val normalCommandMap = command("Normal") {
//        setUp(Input.Keys.SPACE, "Show the scrolling dialog") {
//            CrawlDialog.showDialog(
//                crawlDialog,
//                """
//            This is it.
//            The scrolling pane,
//            the pane of scrolls.T
//            It goes from bottom to up,
//            in a most satisifying
//            temperament
//        """.trimIndent()
//            )
//        }
    }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    private val stage by lazy {
        val aStage = ktx.actors.stage(batch, ExtendViewport(800f, 600f, OrthographicCamera()))
//        aStage.actors {
//            label(scoreAvgs.joinToString() + "\n" + scoresInterpolated.joinToString()).apply {
//                setFontScale(0.5f)
//            }
//        }
        aStage
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

