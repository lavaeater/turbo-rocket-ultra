package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import gamestate.GameEvent
import gamestate.GameState
import isometric.toIsometric
import ktx.graphics.use
import ktx.math.ImmutableVector2
import ktx.math.toMutable
import ktx.math.vec2
import statemachine.StateMachine
import tru.Assets
import kotlin.properties.Delegates.observable

class Line(c: Vector2, l: Float, r: Float = 0f) {
    var x:Float by observable(c.x) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var y:Float by observable(c.y) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var length:Float by observable(l) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var rotation:Float by observable(r) { _, oldValue, newValue -> dirty = newValue != oldValue }
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
    var e1 = ImmutableVector2(0f,0f)
        private set
        get() {
            update()
            return field
        }
    var e2 = ImmutableVector2(0f,0f)
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
    val line = Line(centerPoint, 25f, 0f)
    var zoom = 0f
    var rotation = 0f


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 1.0f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -1.0f })
        setBoth(Input.Keys.A, "Rotate Left", { rotation = 0f }) { rotation = -1.0f }
        setBoth(Input.Keys.D, "Rotate Right", { rotation = 0f }) { rotation = 1.0f }
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

    override fun render(delta: Float) {
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
        line.rotation = line.rotation + rotation
        super.render(delta)
        batch.use {
            shapeDrawer.line(line.e1.toMutable(), line.e2.toMutable(), 1f)
            shapeDrawer.filledCircle(line.e1.toMutable(), 2.5f, Color.GREEN)
            shapeDrawer.filledCircle(line.e2.toMutable(), 2.5f, Color.BLUE)
            shapeDrawer.filledCircle(line.center.toMutable(), 1.5f, Color.RED)

            shapeDrawer.line(line.e1.toMutable().toIsometric(), line.e2.toMutable().toIsometric(), 1f)
            shapeDrawer.filledCircle(line.e1.toMutable().toIsometric(), 2.5f, Color.GREEN)
            shapeDrawer.filledCircle(line.e2.toMutable().toIsometric(), 2.5f, Color.BLUE)
            shapeDrawer.filledCircle(line.center.toMutable().toIsometric(), 1.5f, Color.RED)
        }
    }
}

