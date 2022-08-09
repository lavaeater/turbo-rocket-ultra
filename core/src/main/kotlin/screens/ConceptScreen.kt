package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import gamestate.GameEvent
import gamestate.GameState
import graphics.ContainerGeometry
import graphics.GeometryLine
import input.Button
import isometric.toIsometric
import ktx.collections.GdxArray
import ktx.graphics.use
import ktx.math.minus
import ktx.math.vec2
import ktx.math.vec3
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.*
import java.text.FieldPosition
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * I am fucking wasting my time, again, as per usual.
 *
 * Vectors can be rotated around other points, there is already support for transformations and stuff...
 *
 * I am so tired and anxious. What is the goal here? Am I just wasting my time?
 *
 * Why have centerpoints and other bullshit, when I could just have like three vectors (points)
 * and rotate them all around the center? Wouldn't that be easier?
 */

open class DirtyClass {
    var dirty = true
    fun setDirty(prop: KProperty<*>, oldValue: Any, newValue: Any) {
        if (oldValue != newValue)
            setDirty()
    }

    fun setDirty() {
        dirty = true
    }
}

class PointsCloud: DirtyClass() {
    var worldX by Delegates.observable(0f, ::setDirty)
    var worldY by Delegates.observable(0f, ::setDirty)
    val position = vec2(worldX, worldY)
        get() {
            field.set(worldX, worldY)
            return field
        }
    val points = mutableListOf<Vector2>()

    private val _actualPoints = mutableListOf<Vector2>()
    val actualPoints: List<Vector2>
        get() {
           update()
            return _actualPoints
        }
    var rotation by Delegates.observable(0f, ::setDirty)
    fun rotate(degrees: Float) {
        rotation += degrees
    }

    fun rotateFortyFive() {
        rotate(45f)
    }

    fun addPoint(position: Vector2) {
        points.add(position)
        dirty = true
    }

    fun update() {
        if(dirty) {
            _actualPoints.clear()
            _actualPoints.addAll(points.map { p -> vec2(p.x + worldX, p.y + worldY).rotateAroundDeg(position, rotation) })
            dirty = false
        }
    }
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    val scoutTexture = Texture(Gdx.files.internal("sprites/scout/scout.png"))
    val scoutNE = Animation<TextureRegion>(
        0.1f, GdxArray(Array(5) {
            TextureRegion(scoutTexture, it * 75, 0, 75, 75)
        }), PlayMode.LOOP
    )

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
    val pointsCloud  = PointsCloud().apply {
        points.add(vec2(0f, 50f))
        points.add(vec2(50f, 0f))
    }
//    val line = Line(vec2(12.5f, 0f), vec2(-12.5f, 0f))

    /**
     * Now I think I have figured out another thing that I kind of want.
     *
     * The arms need not change length or direction (useful in and of itself, of course),
     * but rather I want to say that attached to a line's endpoint is the startpoint
     * of another line, relating to the first's geometry somehow, perhaps using a
     * related angle or something. A rotation. A hierarchy of points that describe
     * lines that relate to each other.
     *
     * We are truly and deeply into the weeds, but push on, my friend.
     */

//    val baseGeometry: ContainerGeometry by lazy {
//        val shoulderLine = GeometryLine(vec2(), 15f, 45f)
//        val rightArmLine = GeometryLine(vec2(50f, 50f), 45f, 45f)
//        val cGeom = ContainerGeometry(vec2(), 0f).apply {
//            add(shoulderLine)
//            add(rightArmLine)
//        }
//        cGeom
//    }
    var zoom = 0f
    var rotation = 0f
    var extension = 0f
//    val triangle = Triangle(vec2(), 7.5f, 15f, 15f, 165f)


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 1.0f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -1.0f })
        setBoth(Input.Keys.A, "Rotate Left", { rotation = 0f }) { rotation = 1.0f }
        setBoth(Input.Keys.D, "Rotate Right", { rotation = 0f }) { rotation = -1.0f }
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
        if(button == Buttons.LEFT) {
            pointsCloud.addPoint(mousePosition.cpy())
        }
        return true
    }

    val screenMouse = vec3()
    val mousePosition = vec2()
    val mouseToCenter = vec2()
    fun updateMouse() {
        screenMouse.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(screenMouse)
        mousePosition.set(screenMouse.x, screenMouse.y)
        mouseToCenter.set(mousePosition - pointsCloud.position)
//        baseGeometry.worldRotation = mouseToCenter.angleDeg() - 90f
//        line.rotation = mouseToCenter.angleDeg() - 135f
//        triangle.updateInverseKinematic(mousePosition)
        //triangle.rotation = mouseToCenter.angleDeg()
    }

    var elapsedTime = 0f
    val scoutPosition = vec2()
    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
//        baseGeometry.updateGeometry()
//        triangle.updateA(triangle.a + extension)
//        triangle.position.set(line.e1.toMutable())
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



//            baseGeometry.draw(shapeDrawer)
//
//            pointsCloud.worldX = MathUtils.lerp(pointsCloud.worldX, mousePosition.x, 0.01f)
//            pointsCloud.worldY = MathUtils.lerp(pointsCloud.worldY, mousePosition.y, 0.01f)
            pointsCloud.rotation = pointsCloud.rotation + rotation
            scoutPosition.set(pointsCloud.worldX - 75 / 2, pointsCloud.worldY - 75 / 2)
            batch.draw(scoutNE.getKeyFrame(elapsedTime), scoutPosition.x, scoutPosition.y)
            for(point in pointsCloud.actualPoints) {
                shapeDrawer.filledCircle(point, 1f, Color.RED)
            }
            //shapeDrawer.line(pointsCloud.position, mousePosition, 1f)


            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
//            shapeDrawer.setColor(Color.YELLOW)
//            for (arm in triangle.arms) {
//                shapeDrawer.line(arm.first.toIsometric(), arm.second.toIsometric())
//            }

//            shapeDrawer.filledPolygon(triangle.polygonB)


        }
    }
}

