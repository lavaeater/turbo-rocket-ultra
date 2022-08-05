package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import ktx.math.*
import statemachine.StateMachine
import tru.Assets
import kotlin.math.pow

/**
 * (endPointTwo - endPointOne).angleDeg()(endPointTwo - endPointOne).angleDeg()
 *
 * alpha = arccos((b^2 + c^2 - a^2)/ 2bc)
 * beta = arccos((a^2 + c^2 - b^2)/ 2ac)
 * gamma = arccos((a^2 + b^2 - c^2) / 2ab)
 */
class Triangle(val position:Vector2, val b: Float, val c: Float, minA: Float, maxA: Float) {

    val cornerA = vec2()
    private val aRange = minA..maxA
    var a = (minA + maxA) / 2f
    val cornerB = vec2()
    val cornerC = vec2()
    val alphaRange = 15f..115f
    var alpha = 0f

    var rotation = 0f
        set(value) {
            field = value
            update()
        }
    val polygonB = Polygon(
        floatArrayOf(
            cornerC.x, cornerC.y,
            cornerB.x, cornerB.y,
            cornerA.x, cornerA.y
        )
    )

    init {
        tryUpdateAlpha(alphaRange.start)
        update()
    }

    fun updateA(newA: Float) {
        if (newA != a && tryUpdateAlpha(newA)) {
            a = newA
            update()
        }
    }

    fun tryUpdateAlpha(newA: Float): Boolean {
        val something = (b.pow(2) + c.pow(2) - newA.pow(2)) / (2 * b * c)
        val angle = MathUtils.acos(something) * radiansToDegrees
        if (alphaRange.contains(angle)) {
            alpha = angle
            return true
        }
        return false
    }

    fun update() {
        val vB = vec2(b).rotateAroundDeg(Vector2.Zero, alpha)
        cornerC.set(cornerA + vB)
        cornerB.set(cornerA + vec2(c))
        updatePolygon()
    }

    fun updatePolygon() {
        polygonB.setOrigin(cornerC.x, cornerC.y)
        polygonB.setVertex(2, cornerA.x, cornerA.y)
        polygonB.setVertex(0, cornerC.x, cornerC.y)
        polygonB.setVertex(1, cornerB.x, cornerB.y)
        polygonB.setPosition(position.x - polygonB.originX, position.y - polygonB.originY)
        polygonB.rotation = rotation
    }

    val arms: Array<Pair<Vector2, Vector2>> = arrayOf(Pair(vec2(), vec2()), Pair(vec2(), vec2()))
        get() {
            updateArms(field)
            return field
    }

    private fun updateArms(arms: Array<Pair<Vector2, Vector2>>) {
        polygonB.getVertex(0, arms[0].first)
        polygonB.getVertex(2, arms[0].second)
        polygonB.getVertex(2, arms[1].first)
        polygonB.getVertex(1, arms[1].second)
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
    var extension = 0f
    val triangle = Triangle(vec2(), 7.5f, 15f, 1.5f, 25f)


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 1.0f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -1.0f })
        setBoth(Input.Keys.A, "Rotate Left", { rotation = 0f }) { rotation = -1.0f }
        setBoth(Input.Keys.D, "Rotate Right", { rotation = 0f }) { rotation = 1.0f }
        setBoth(Input.Keys.W, "Extend", { extension = 0f }) { extension = .1f }
        setBoth(Input.Keys.S, "Reverse", { extension = 0f }) { extension = -.1f }
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
        //line.rotation = mouseToCenter.angleDeg() - 135f
        triangle.rotation = mouseToCenter.angleDeg()
    }

    override fun render(delta: Float) {
        updateMouse()
        triangle.updateA(triangle.a + extension)
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
//        line.rotation = line.rotation + rotation
        super.render(delta)
        batch.use {
//            shapeDrawer.line(line.e1.toMutable(), line.e2.toMutable(), 1f)
//            shapeDrawer.filledCircle(line.e1.toMutable(), 2.5f, Color.GREEN)
//            shapeDrawer.filledCircle(line.e2.toMutable(), 2.5f, Color.BLUE)
//            shapeDrawer.filledCircle(line.center.toMutable(), 1.5f, Color.RED)

//            shapeDrawer.line(line.e1.toMutable().toIsometric(), line.e2.toMutable().toIsometric(), 1f)
//            shapeDrawer.filledCircle(line.e1.toMutable().toIsometric(), 2.5f, Color.GREEN)
//            shapeDrawer.filledCircle(line.e2.toMutable().toIsometric(), 2.5f, Color.BLUE)
//            shapeDrawer.filledCircle(line.center.toMutable().toIsometric(), 1.5f, Color.RED)
            shapeDrawer.line(triangle.position, mousePosition, 1f)


            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
            shapeDrawer.setColor(Color.YELLOW)
            for(arm in triangle.arms) {
                shapeDrawer.line(arm.first, arm.second)
            }

//            shapeDrawer.filledPolygon(triangle.polygonB)


        }
    }
}

