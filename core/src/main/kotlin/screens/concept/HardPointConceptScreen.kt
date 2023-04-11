package screens.concept

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.*
import screens.basic.BasicScreen
import screens.command.command
import screens.ui.KeyPress
import screens.ui.MousePosition
import statemachine.StateMachine
import tru.AnimState
import tru.Assets
import tru.CardinalDirection
import kotlin.math.absoluteValue

class HardPointConceptScreen(
    gameState: StateMachine<GameState, GameEvent>
) : BasicScreen(gameState) {
    private val character = Character().apply {
        worldPosition.set(200f, 200f)
    }
    private val characterAnim = Assets.characterTurboAnims.first { it.key == "boy" }
    private var stateTime = 0f
    private var rotation = 0

    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.LEFT, "Rotate Left", { rotation = 0 }, { rotation = 1 })
        setBoth(Input.Keys.RIGHT, "Rotate Right", { rotation = 0 }, { rotation = -1 })
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

    private val rotationSpeed = 100f
    private fun updateCharacter(delta: Float) {
        character.aimVector.set(MousePosition.worldPosition2D - character.worldAnchors["rightshoulder"]!!).nor()
        character.forward.setAngleDeg((MousePosition.worldPosition2D - character.worldPosition).angleDeg())

        if (rotation != 0)
            character.forward.rotateDeg(rotation * rotationSpeed * delta)
    }

    override fun show() {
        super.show()
        viewport.worldWidth = 800f
        viewport.worldHeight = 600f
        camera.setToOrtho(false)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        batch.projectionMatrix = camera.combined
    }

    override fun render(delta: Float) {
        camera.position.set(character.worldPosition.x, character.worldPosition.y, 0f)
        updateCharacter(delta)
        super.render(delta)
        renderInternal(delta)
    }

    private val drawMethods = mapOf(
        CardinalDirection.East to listOf(::drawRegion, ::drawRifle),
        CardinalDirection.South to listOf(::drawRegion, ::drawRifle),
        CardinalDirection.West to listOf(::drawRifle, ::drawRegion),
        CardinalDirection.North to listOf(::drawRifle, ::drawRegion)
    )

    private fun drawRegion(character: Character) {
        val region =
            characterAnim.animations[AnimState.Idle]!!.animations[character.cardinalDirection]!!.getKeyFrame(
                stateTime
            )
        batch.draw(
            region,
            character.worldPosition.x - region.regionWidth / 2f,
            character.worldPosition.y - region.regionHeight / 2f
        )
    }

    private fun renderInternal(delta: Float) {
        stateTime += delta
        batch.use {
            for(drawMethod in drawMethods[character.cardinalDirection]!!)
                drawMethod(character)
        }
    }

    private val rifle = Polygon(floatArrayOf(0f, 2f, 50f, 2f, 50f, -2f, 0f, -2f))

    private fun drawRifle(character: Character) {
        drawHandsAndArms(character)
        val start = character.worldAnchors["rightshoulder"]!!.cpy()
        rifle.rotation = character.aimVector.angleDeg()
        rifle.setPosition(start.x, start.y)
        val scaleX = MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue)
        rifle.setScale(scaleX, 1f)

        shapeDrawer.setColor(Color.GRAY)
        shapeDrawer.filledPolygon(rifle)
        shapeDrawer.setColor(Color.WHITE)
    }

    private val skinColor = Color(0.8f,0.6f, 0.5f, 1f)

    private fun drawHandsAndArms(character: Character) {
        val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
        val rightHandPosition = rightShoulder + character.aimVector.cpy().scl(5f)
        shapeDrawer.filledCircle(rightHandPosition, 4f, skinColor)
        val leftHandPosition = rightShoulder + character.aimVector.cpy().scl(15f)
        shapeDrawer.filledCircle(leftHandPosition, 4f, skinColor)
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

    val cardinalAbsoluteAngles = mapOf(
        CardinalDirection.East to 0f,
        CardinalDirection.South to 270f,
        CardinalDirection.West to 180f,
        CardinalDirection.North to 90f
    )

    val cardinalVectors = mapOf(
        CardinalDirection.East to vec2(1f, 0f),
        CardinalDirection.South to vec2(0f, -1f),
        CardinalDirection.West to vec2(-1f, 0f),
        CardinalDirection.North to vec2(0f, 1f)
    )

    fun angleToCardinal(angle: Float): CardinalDirection {
        val dirKey = cardinals.keys.firstOrNull { it.contains(angle) }
        return if (dirKey != null) cardinals[dirKey]!! else CardinalDirection.South
    }
}

class Character {
    var worldPosition = vec2()
    val direction = Direction()
    val forward = direction.forward

    val angleDegrees get() = direction.angleDegrees
    val cardinalDirection get() = direction.cardinalDirection
    var width = 32f
    var height = 32f
    var scale = 1.0f
    val center = Vector2.Zero.cpy()

    private val anchors = mapOf(
        "rightshoulder" to vec2(0.1f, 0.5f),
        "leftshoulder" to vec2(0.1f, -0.5f)
    )

    var aimVector = Vector2.X.cpy()

    //the angle should be the cardinal directions angle for the anchor points - they are static!
    val worldAnchors
        get() = anchors.map {
            it.key to worldPosition + vec2().set(it.value.x, it.value.y * 0.5f).times(32f)
                .setAngleDeg(direction.cardinalAngle - it.value.angleDeg())
        }.toMap()

}


class Direction {
    val forward = vec2(1f, 0f)
    var backward: Vector2 = vec2()
        get() = field.set(forward).rotateDeg(180f)
        private set
    var left: Vector2 = vec2()
        get() = field.set(forward).rotateDeg(90f)
        private set
    var right: Vector2 = vec2()
        get() = field.set(forward).rotateDeg(-90f)
        private set
    val angleDegrees get() = forward.angleDeg()
    val cardinalDirection get() = CardinalToAngles.angleToCardinal(angleDegrees)
    val cardinalAngle get() = CardinalToAngles.cardinalAbsoluteAngles[cardinalDirection]!!
    val cardinalForward get() = CardinalToAngles.cardinalVectors[cardinalDirection]!!
}

