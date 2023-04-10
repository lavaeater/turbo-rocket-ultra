package screens.concept

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.math.vec2
import ktx.scene2d.actors
import ktx.scene2d.dialog
import ktx.scene2d.label
import ktx.scene2d.scene2d
import screens.basic.BasicScreen
import screens.command.command
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.Assets
import tru.CardinalDirection
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

    fun renderInternal(delta: Float) {

    }
}

object CardinalToAngles {
    val cardinals = mapOf(
        241f..300f to CardinalDirection.South,
        301f..360f to CardinalDirection.East,
        0f..60f to CardinalDirection.East,
        61f..120f to CardinalDirection.North,
        121f..240f to CardinalDirection.West
    )

    fun angleToCardinal(angle:Float): CardinalDirection {
        val dirKey = cardinals.keys.firstOrNull { it.contains(angle) }
        return if(dirKey != null) cardinals[dirKey]!! else CardinalDirection.South
    }
}

class Character {
    var direction: CardinalDirection = CardinalDirection.South
    var worldPosition = vec2()
    val worldDirection = vec2(0f,-1f)
    var width = 32f
    var height = 32f
    var scale = 1.0f
    val center = Vector2.Zero.cpy()
    val rightShoulderLocal = vec2(-0.5f, 0f)
    val leftShoulderLocal = vec2(0.5f, 0f)

}
