package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import gamestate.GameEvent
import gamestate.GameState
import isometric.toIsometric
import ktx.graphics.use
import ktx.math.minus
import ktx.math.vec2
import ktx.math.vec3
import space.earlygrey.shapedrawer.ShapeDrawer
import statemachine.StateMachine
import tru.Assets
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

abstract class Geometry(offset: Vector2 = vec2(), rotation: Float = 0f) {
    var worldX: Float by Delegates.observable(offset.x, ::setDirty)
    var worldY: Float by Delegates.observable(offset.y, ::setDirty)
    var localX: Float by Delegates.observable(offset.x, ::setDirty)
    var localY: Float by Delegates.observable(offset.y, ::setDirty)
    val worldPosition: Vector2 = vec2(worldX, worldY)
        get() {
            field.set(worldX, worldY)
            return field
        }
    var worldRotation: Float by Delegates.observable(rotation, ::setDirty)
    var localRotation: Float by Delegates.observable(rotation, ::setDirty)
    val children = mutableListOf<Geometry>()
    var dirty = true

    /**
     * Call this method from any properties that, when changed,
     * will require recalculation of any other properties etc.
     */
    fun setDirty(prop: KProperty<*>, oldValue: Any, newValue: Any) {
        if (oldValue != newValue)
            setDirty()
    }


    private fun setDirty() {
        dirty = true
    }

    /**
     * Called from parent or engine with some kind of baseposition or something
     * I suppose
     */
    protected fun update(parentPosition: Vector2 = Vector2.Zero, parentRotation: Float = 0f) {
        worldX = parentPosition.x + localX
        worldY = parentPosition.y + localY
        worldRotation = parentRotation + localRotation
        updateSelfIfDirty()
        updateChildren()
    }

    private fun updateSelfIfDirty() {
        if (dirty) {
            updateSelf()
            dirty = false
        }
    }

    /**
     * Override with functionality to update the geometry object
     */
    abstract fun updateSelf()

    fun updateChildren() {
        for (child in children) {
            child.update(worldPosition, worldRotation)
        }
    }

    abstract fun draw(shapeDrawer: ShapeDrawer)
}

class ContainerGeometry(position: Vector2, rotation: Float) : Geometry(position, rotation) {
    override fun updateSelf() {
    }

    fun updateGeometry() {
        update(worldPosition, worldRotation)
    }

    override fun draw(shapeDrawer: ShapeDrawer) {
        for (child in children) {
            child.draw(shapeDrawer)
        }
    }
}

class GeometryLine(c: Vector2, l: Float, val r: Float = 0f) : Geometry(c, r) {
    constructor(
        endPointOne: Vector2,
        endPointTwo: Vector2
    ) : this(
        vec2(
            endPointTwo.x - (endPointTwo.x - endPointOne.x) / 2f,
            endPointTwo.y - (endPointTwo.y - endPointOne.y) / 2f
        ), (endPointOne - endPointTwo).len(), (endPointTwo - endPointOne).angleDeg()
    )

    val actualRotation get() = worldRotation + r
    var length: Float by Delegates.observable(l, ::setDirty)
    var e1 = Vector2(0f, 0f)
    var e2 = Vector2(0f, 0f)

    override fun updateSelf() {
        val lv = vec2(length / 2f).rotateAroundDeg(vec2(0f, 0f), actualRotation)
        val ex = worldPosition.x + lv.x
        val ey = worldPosition.y + lv.y
        e1.set(ex, ey)
        e2.set(-ex, -ey)
    }

    override fun draw(shapeDrawer: ShapeDrawer) {
        shapeDrawer.line(e1.toIsometric(), e2.toIsometric(), 1f)
        shapeDrawer.filledCircle(e1.toIsometric(), 2.5f, Color.GREEN)
        shapeDrawer.filledCircle(e2.toIsometric(), 2.5f, Color.BLUE)
        shapeDrawer.filledCircle(worldPosition.toIsometric(), 1.5f, Color.RED)
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

    val baseGeometry = ContainerGeometry(vec2(), 0f).apply {
        children.add(GeometryLine(vec2(), 25f, 90f))
    }


    var zoom = 0f
    var rotation = 0f
    var extension = 0f
//    val triangle = Triangle(vec2(), 7.5f, 15f, 15f, 165f)


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
        mouseToCenter.set(mousePosition - baseGeometry.worldPosition)
        baseGeometry.worldRotation = mouseToCenter.angleDeg()
        baseGeometry.updateGeometry()
//        line.rotation = mouseToCenter.angleDeg() - 135f
//        triangle.updateInverseKinematic(mousePosition)
        //triangle.rotation = mouseToCenter.angleDeg()
    }

    override fun render(delta: Float) {
        updateMouse()
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

            baseGeometry.draw(shapeDrawer)

            shapeDrawer.line(baseGeometry.worldPosition, mousePosition, 1f)


            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
//            shapeDrawer.setColor(Color.YELLOW)
//            for (arm in triangle.arms) {
//                shapeDrawer.line(arm.first.toIsometric(), arm.second.toIsometric())
//            }

//            shapeDrawer.filledPolygon(triangle.polygonB)


        }
    }
}

