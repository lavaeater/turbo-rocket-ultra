package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.math.Vector2
import gamestate.GameEvent
import gamestate.GameState
import isometric.toIsometric
import ktx.graphics.use
import ktx.math.*
import statemachine.StateMachine
import tru.Assets
import kotlin.math.pow
import kotlin.properties.Delegates.observable
import kotlin.properties.Delegates.vetoable

/**
 * (endPointTwo - endPointOne).angleDeg()(endPointTwo - endPointOne).angleDeg()
 *
 * alpha = arccos((b^2 + c^2 - a^2)/ 2bc)
 * beta = arccos((a^2 + c^2 - b^2)/ 2ac)
 * gamma = arccos((a^2 + b^2 - c^2) / 2ab)
 */
class Triangle(val cornerA: Vector2, val sideB: Float, val sideC: Float, val minA: Float, val maxA: Float) {
    init {
        update()
    }

    var dirty = true
    fun update() {
        if (dirty) {
            alpha =
                MathUtils.acos(((sideB.pow(2) + sideC.pow(2) - sideAActual.pow(2)) / (2 * sideB * sideC)) * degreesToRadians) * radiansToDegrees - 90f
            beta =
                MathUtils.acos(((sideAActual.pow(2) + sideC.pow(2) - sideB.pow(2)) / (2 * sideAActual * sideC)) * degreesToRadians) * radiansToDegrees - 90f// * radiansToDegrees
            gamma =
                MathUtils.acos(((sideAActual.pow(2) + sideB.pow(2) - sideC.pow(2)) / (2 * sideAActual * sideB)) * degreesToRadians) * radiansToDegrees - 90f// * radiansToDegrees

            val sbv = vec2(sideB).rotateDeg(alpha)
            cornerC = ImmutableVector2(cornerA.x + sbv.x, cornerA.y + sbv.y)
            val sav = vec2(sideAActual).rotateDeg(-beta)
            cornerB = ImmutableVector2(cornerA.x + sbv.x + sav.x, cornerA.y + sbv.y - sav.y)
            dirty = false
        }
    }

    var sideAActual = (minA + maxA) / 2f
    var sideA: Float by vetoable((minA + maxA) / 2f) { p, old, new ->
        if ((minA..maxA).contains(new) && new != old) {
            sideAActual = new
            dirty = true
            true
        }
        false
    }
    var direction by observable(0f) { _, oldValue, newValue ->
        dirty = newValue != oldValue
    }
    var alpha = 60f
        private set
    var beta = 60f
        private set
    var gamma = 60f
        private set
    var cornerB = ImmutableVector2(0f, 0f)
        private set
        get() {
            update()
            return field
        }
    var cornerC = ImmutableVector2(0f, 0f)
        private set
        get() {
            update()
            return field
        }
}

class Line(c: Vector2, l: Float, r: Float = 0f) {
    constructor(
        endPointOne: Vector2,
        endPointTwo: Vector2
    ) : this(
        vec2(
            endPointTwo.x - (endPointTwo.x - endPointOne.x) / 2f,
            endPointTwo.y - (endPointTwo.y - endPointOne.y) / 2f
        ), (endPointOne - endPointTwo).len(), (endPointTwo - endPointOne).angleDeg()
    )

    var x: Float by observable(c.x) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var y: Float by observable(c.y) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var length: Float by observable(l) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var rotation: Float by observable(r) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var dirty = true

    fun update() {
        if (dirty) {
            center = ImmutableVector2(x, y)
            val lv = vec2(length / 2f).rotateAroundDeg(vec2(0f, 0f), rotation)
            val ex = x + lv.x
            val ey = y + lv.y
            e1 = ImmutableVector2(ex, ey)
            e2 = ImmutableVector2(-ex, -ey)
            dirty = false
        }
    }

    var center = ImmutableVector2(x, y)
        private set
        get() {
            update()
            return field
        }
    var e1 = ImmutableVector2(0f, 0f)
        private set
        get() {
            update()
            return field
        }
    var e2 = ImmutableVector2(0f, 0f)
        private set
        get() {
            update()
            return field
        }
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    /**
     * What we want to do is basically what we did for anchor points, but perhaps
     * with some tweaking?
     *
     * We want two points on the end of a line. They are, like everything else, always
     * projected into an isometric space.
     *
     * So, the points are equidistant from a center point, which is a point, 100,100
     */

    val centerPoint = vec2(0f, 0f)
    val line = Line(vec2(12.5f, 0f), vec2(-12.5f, 0f))
    var zoom = 0f
    var rotation = 0f
    val triangle = Triangle(vec2(), 5f, 5f, 15f, 30f)


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 1.0f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -1.0f })
        setBoth(Input.Keys.A, "Rotate Left", { rotation = 0f }) { rotation = -1.0f }
        setBoth(Input.Keys.D, "Rotate Right", { rotation = 0f }) { rotation = 1.0f }
        setUp(Input.Keys.W, "Extend") { triangle.sideA += 1f }
        setUp(Input.Keys.S, "Reverse") { triangle.sideA -= 1f }
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

    val screenMouse = vec3()
    val mousePosition = vec2()
    val mouseToCenter = vec2()
    fun updateMouse() {
        screenMouse.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(screenMouse)
        mousePosition.set(screenMouse.x, screenMouse.y)
        mouseToCenter.set(mousePosition - line.center.toMutable())
        line.rotation = mouseToCenter.angleDeg() - 135f
    }

    override fun render(delta: Float) {
        updateMouse()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
//        line.rotation = line.rotation + rotation
        super.render(delta)
        batch.use {
            shapeDrawer.line(line.e1.toMutable(), line.e2.toMutable(), 1f)
            shapeDrawer.filledCircle(line.e1.toMutable(), 2.5f, Color.GREEN)
            shapeDrawer.filledCircle(line.e2.toMutable(), 2.5f, Color.BLUE)
            shapeDrawer.filledCircle(line.center.toMutable(), 1.5f, Color.RED)

//            shapeDrawer.line(line.e1.toMutable().toIsometric(), line.e2.toMutable().toIsometric(), 1f)
//            shapeDrawer.filledCircle(line.e1.toMutable().toIsometric(), 2.5f, Color.GREEN)
//            shapeDrawer.filledCircle(line.e2.toMutable().toIsometric(), 2.5f, Color.BLUE)
//            shapeDrawer.filledCircle(line.center.toMutable().toIsometric(), 1.5f, Color.RED)
            shapeDrawer.line(line.center.toMutable(), mousePosition, 1f)

            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
//            shapeDrawer.filledCircle(mousePosition.toIsometric(), 1.5f, Color.GREEN)

            shapeDrawer.line(triangle.cornerA, triangle.cornerC.toMutable(), Color.GREEN)
            shapeDrawer.line(triangle.cornerC.toMutable(), triangle.cornerB.toMutable(), Color.GREEN)
            shapeDrawer.line(triangle.cornerB.toMutable(), triangle.cornerA, Color.GREEN)
        }
    }
}

