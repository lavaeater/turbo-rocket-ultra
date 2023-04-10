package screens.concept

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2
import screens.basic.BasicScreen
import screens.command.command
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.AnimState
import tru.Assets
import tru.CardinalDirection

class HardPointConceptScreen(
    gameState: StateMachine<GameState, GameEvent>
) : BasicScreen(gameState) {
    private val character = Character().apply {
        worldPosition.set(200f, 200f)
    }
    private val characterAnim = Assets.characterTurboAnims.first { it.key == "boy" }
    private var stateTime = 0f

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
        camera.position.set(character.worldPosition.x, character.worldPosition.y, 0f)
        super.render(delta)
        stage.act()
        stage.draw()
        renderInternal(delta)
    }

    fun renderInternal(delta: Float) {
        stateTime += delta
        batch.use {
            val region =
                characterAnim.animations[AnimState.Idle]!!.animations[character.cardinalDirection]!!.getKeyFrame(
                    stateTime
                )
            it.draw(region, character.worldPosition.x, character.worldPosition.y)
            for ((key, point) in character.worldAnchors) {
                shapeDrawer.filledCircle(point, 5f, if(key.contains("left")) Color.RED else Color.GREEN)
            }
        }
    }
}

object CardinalToAngles {
    private val cardinals = mapOf(
        241f..300f to CardinalDirection.South,
        301f..360f to CardinalDirection.East,
        0f..60f to CardinalDirection.East,
        61f..120f to CardinalDirection.North,
        121f..240f to CardinalDirection.West
    )

    fun angleToCardinal(angle: Float): CardinalDirection {
        val dirKey = cardinals.keys.firstOrNull { it.contains(angle) }
        return if (dirKey != null) cardinals[dirKey]!! else CardinalDirection.South
    }
}

class Character {
    var worldPosition = vec2()
    val worldDirection = vec2(1f, 0f)
    val angleDegrees get() = worldDirection.angleDeg()
    val cardinalDirection get() = CardinalToAngles.angleToCardinal(angleDegrees)
    var width = 32f
    var height = 32f
    var scale = 1.0f
    val center = Vector2.Zero.cpy()

    val anchors = mapOf(
        "rightshoulder" to vec2(0f, -0.25f),
        "leftshoulder" to vec2(0f, 0.25f)
    )

    //the angle should be the cardinal directions angle for the anchor points - they are static!
    val worldAnchors get() = anchors.map {
        it.key to worldPosition + it.value * 32f
    }.toMap()

}
