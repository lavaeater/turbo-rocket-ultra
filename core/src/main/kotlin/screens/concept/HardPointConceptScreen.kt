package screens.concept

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.minus
import ktx.math.plus
import screens.basic.BasicScreen
import screens.command.command
import screens.ui.KeyPress
import screens.ui.MousePosition
import statemachine.StateMachine
import tru.AnimState
import tru.Assets
import tru.CardinalDirection
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.absoluteValue
import kotlin.math.pow

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
        CardinalDirection.East to listOf(::drawLeftHand, ::drawRegion, ::drawRifle, ::drawRightHand),
        CardinalDirection.South to listOf(::drawRegion, ::drawLeftHand, ::drawRightHand, ::drawRifle),
        CardinalDirection.West to listOf(::drawRightHand, ::drawRifle, ::drawRegion, ::drawLeftHand),
        CardinalDirection.North to listOf(::drawLeftHand, ::drawRightHand, ::drawRifle, ::drawRegion)
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

    fun drawAnchors(character: Character) {
        for ((key, point) in character.worldAnchors) {
            shapeDrawer.filledCircle(
                point,
                5f,
                if (key.contains("left")) Color.RED else Color.GREEN
            )
        }
    }

    private fun renderInternal(delta: Float) {
        stateTime += delta
        batch.use {
            for (drawMethod in drawMethods[character.cardinalDirection]!!)
                drawMethod(character)

        }
    }

    private fun renderAimVector() {
        shapeDrawer.filledCircle(character.worldPosition + (character.aimVector.cpy().scl(25f)), 2.5f, Color.YELLOW)
    }

    private fun renderLineToMouse() {
        val start = character.worldPosition + (character.aimVector.cpy().scl(25f))
        val stop = MousePosition.worldPosition2D.cpy()
        shapeDrawer.line(start, stop, Color.YELLOW)
        shapeDrawer.filledCircle(start, 2f, Color.GREEN)
        shapeDrawer.filledCircle(stop, 2f, Color.RED)
        shapeDrawer.filledCircle(MousePosition.worldPosition2D, 5f, Color.WHITE)

        start.set(character.worldAnchors["rightshoulder"]!!)

        shapeDrawer.line(start, stop, Color.RED)
    }

    private val rifle = Polygon(floatArrayOf(0f, 2f, 50f, 2f, 50f, -2f, 0f, -2f))

    private fun drawRifle(character: Character) {
        val start = character.worldAnchors["rightshoulder"]!!.cpy()
        rifle.rotation = character.aimVector.angleDeg()
        rifle.setPosition(start.x, start.y)
        val scaleX = MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue)
        rifle.setScale(scaleX, 1f)

        shapeDrawer.setColor(Color.GRAY)
        shapeDrawer.filledPolygon(rifle)
        shapeDrawer.setColor(Color.WHITE)
    }

    private val skinColor = Color(0.8f, 0.6f, 0.5f, 1f)

    fun drawLeftHand(character: Character) {
        val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
        val leftShoulder = character.worldAnchors["leftshoulder"]!!.cpy()
        val lowerArmLength = 24f
        val upperArmLength = lowerArmLength * 5f / 8f
        val leftGripLength = 20f
        val leftHandDirection = character.aimVector.cpy().scl(leftGripLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
        val leftHandGripPoint = rightShoulder + leftHandDirection
        shapeDrawer.filledCircle(leftHandGripPoint, 4f, skinColor)
        leftHandDirection.set(leftHandGripPoint).sub(leftShoulder).nor()
        val leftDistance = leftHandGripPoint.dst(leftShoulder)

        val beta =
            MathUtils.acos((leftDistance.pow(2) + upperArmLength.pow(2) - lowerArmLength.pow(2)) / (2 * leftDistance * upperArmLength))
        val leftUpperArmVector = leftHandDirection.cpy().rotateRad(beta).scl(upperArmLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
        shapeDrawer.line(leftShoulder, leftShoulder + leftUpperArmVector, Color.BROWN, 6f)
        shapeDrawer.line(leftShoulder + leftUpperArmVector, leftHandGripPoint, Color.BROWN, 6f)
    }

    fun drawRightHand(character: Character) {
        val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
        val rightGripLength = 8f
        val rightHandDirection = character.aimVector.cpy().scl(rightGripLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
        val rightHandGripPoint = rightShoulder + rightHandDirection
        shapeDrawer.filledCircle(rightHandGripPoint, 4f, skinColor)

        val lowerArmLength = 12f
        val upperArmLength = lowerArmLength * 5f / 8f
        rightHandDirection.set(rightHandGripPoint).sub(rightShoulder).nor()
        val rightDistance = rightHandGripPoint.dst(rightShoulder)

        val beta =
            MathUtils.acos((rightDistance.pow(2) + upperArmLength.pow(2) - lowerArmLength.pow(2)) / (2 * rightDistance * upperArmLength))

        val rightUpperArmVector = rightHandDirection.cpy().rotateRad(-beta).scl(upperArmLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
        shapeDrawer.line(rightShoulder, rightShoulder + rightUpperArmVector, Color.BROWN, 6f)
        shapeDrawer.line(rightShoulder + rightUpperArmVector, rightHandGripPoint, Color.BROWN, 6f)
    }
}




