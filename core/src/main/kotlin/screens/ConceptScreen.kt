package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import isometric.toIsometric
import ktx.collections.toGdxArray
import ktx.graphics.use
import ktx.math.*
import screens.ui.KeyPress
import space.earlygrey.shapedrawer.ShapeDrawer
import statemachine.StateMachine
import tru.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable
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
 *
 * Should I even do it like this, eh?
 */

/**
 * A new idea for all of this, going back to what we had previously, is of course
 * the hierarchical geometry, point clouds etc.
 *
 * But isn't that basically spine? What are we doing here? Will there ever be a new
 * character sprite in this game? Do I want the scout character?
 *
 * Let's make a sort of requirement spec for the character sprite, in Obsidian
 */

open class DirtyClass {
    var dirty = true
    fun setDirty(prop: KProperty<*>, oldValue: Any?, newValue: Any?) {
        if (oldValue != newValue)
            setDirty()
    }

    open fun setDirty() {
        dirty = true
    }
}

class Node : DirtyClass() {
    var color = Color.RED
    var parent: Node? by observable(null, ::setDirty)
    var position: ImmutableVector2 by observable(ImmutableVector2(0f, 0f), ::setDirty)
    var actualPosition = vec2()
    var rotation by observable(0f, ::setDirty)
    val children = mutableListOf<Node>()
    override fun setDirty() {
        super.setDirty()
        for (childNode in children) {
            childNode.setDirty()
        }
    }

    fun addChild(childNode: Node) {
        childNode.parent = this
        children.add(childNode)
        setDirty()
    }

    fun removeChild(childNode: Node) {
        if (children.remove(childNode)) {
            childNode.parent = null
            setDirty()
        }
    }

    var rotateWithParent by observable(true, ::setDirty)
    var updateAction: (Node, Float) -> Unit = { _, _ -> }
    fun update(delta: Float) {
        if (dirty) {
            updateAction(this, delta)
            if (parent != null) {
                actualPosition.set(parent!!.actualPosition + position.toMutable())
                if (rotateWithParent) {
                    actualPosition.rotateAroundDeg(parent!!.actualPosition, parent!!.rotation)
                    rotation = actualPosition.angleDeg()
                }
            } else {
                actualPosition.set(position.toMutable())
            }
        }
        for (childNode in children) {
            childNode.update(delta)
        }
    }

    fun drawIso(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float) {
        shapeDrawer.filledCircle(actualPosition.toIsometric(), 1f, color)
        for (childNode in children) {
            childNode.drawIso(batch, shapeDrawer, delta)
        }
    }

    fun draw(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float) {
        shapeDrawer.filledCircle(actualPosition, 5f, color)
        for (childNode in children) {
            childNode.draw(batch, shapeDrawer, delta)
        }
        if (parent != null) {
            shapeDrawer.setColor(color)
            shapeDrawer.line(actualPosition, parent!!.actualPosition, 1f)
            shapeDrawer.setColor(Color.WHITE)
        }
    }
}

class PointsCloud : DirtyClass() {
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
        if (dirty) {
            _actualPoints.clear()
            _actualPoints.addAll(points.map { p ->
                vec2(p.x + worldX, p.y + worldY).rotateAroundDeg(
                    position,
                    rotation
                )
            })
            dirty = false
        }
    }
}

/**
 * My graphics can look any way they want - as long as they are unique and interesting.
 *
 * So, lets try triangles for legs,
 *
 * Lets try it out by drawing just dots and stuff for now.
 */

open class AnimatedSprite(texture: Texture) : TextureRegion(texture) {
    /*
    We assume a center based origin at first.
     */
    val position = vec2()
    val offset = vec2(texture.width / 2f, texture.height / 2f)
    val actualPosition: Vector2
        get() {
            return position - offset
        }
}

class LayeredCharacter {
    val position = vec2() //This is the center of the character, then head should be about one "meter" above this.
    val headOffset = vec2(0f, 0.8f)
    val leftEye = vec2(0.1f)
    val rightEye = vec2(-0.1f)
    val head by lazy { TextureRegion(Texture(Gdx.files.internal("sprites/layered/head.png"))) }
    val eye by lazy { TextureRegion(Texture(Gdx.files.internal("sprites/layered/eye.png"))) }
    fun draw(batch: Batch, delta: Float) {

    }
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    /**
     * Idle : 0-7
     * Shoot: 8-10
     * Run: 11-18
     * Reload: 19-31
     * Death: 32-43
     */
    override val viewport = ExtendViewport(16f, 12f)

    val nodeTree = Node().apply {
        addChild(Node().apply {
            color = Color.YELLOW
            position = ImmutableVector2(10f, 10f)
            addChild(Node().apply {
                position = ImmutableVector2(30f, -20f)
                rotateWithParent = false
                color = Color.ORANGE
            })
        })
        addChild(Node().apply {
            color = Color.GREEN
            position = ImmutableVector2(30f, -20f)
            addChild(Node().apply {
                val basePosition = position
                var elapsedTime = 0f
                var previousTime = 0f
                val timeStep = 0.1f
                val steps = 12
                val modifier = -15f..15f
                var currentStep = 0
                updateAction = { node, delta ->
                    elapsedTime += delta
                    val diff = elapsedTime - previousTime
                    if (diff > timeStep) {
                        previousTime = elapsedTime
                        if (currentStep >= steps) {
                            currentStep = 0
                        }
                        val forward = currentStep < steps / 2 - 1
                        val modValue = if (forward) MathUtils.lerp(
                            modifier.start,
                            modifier.endInclusive,
                            ((currentStep.toFloat() + 1f) / (steps / 2f))
                        ) else MathUtils.lerp(
                            modifier.endInclusive,
                            modifier.start,
                            ((currentStep.toFloat() - (steps / 2f) + 1f) / (steps / 2f))
                        )
                        position = ImmutableVector2(basePosition.x + modValue, basePosition.y + modValue)
                        currentStep++
                    }
                }
                position = ImmutableVector2(30f, -20f)
                color = Color.GREEN
            })
        })
    }


    val offsetX = 0
    val padX = 0
    val padY = 0
    val offsetY = 0
    val scoutHeight = 150
    val scoutWidth = 150
    val scoutTextures = mapOf(
        CardinalDirection.East to Texture(Gdx.files.internal("sprites/scout/scout_e_small.png")),
        CardinalDirection.NorthEast to Texture(Gdx.files.internal("sprites/scout/scout_ne_small.png")),
        CardinalDirection.North to Texture(Gdx.files.internal("sprites/scout/scout_n_small.png")),
        CardinalDirection.SouthEast to Texture(Gdx.files.internal("sprites/scout/scout_se_small.png")),
        CardinalDirection.South to Texture(Gdx.files.internal("sprites/scout/scout_s_small.png")),
        CardinalDirection.SouthWest to Texture(Gdx.files.internal("sprites/scout/scout_sw_small.png")),
    )

    val animStates = mapOf(
        AnimState.Idle to Pair(0, 8),
        AnimState.Shoot to Pair(1, 3),
        AnimState.Walk to Pair(2, 8),
        AnimState.Death to Pair(4, 11)
    )

    val scoutAnims: Map<AnimState, LpcCharacterAnim<TextureRegion>> = animStates.map { state ->
        state.key to LpcCharacterAnim(
            state.key, CardinalDirection.scoutDirections.map { direction ->
                direction to Animation(1f / 8f, Array(state.value.second) { frame ->
                    TextureRegion(
                        scoutTextures[direction]!!,
                        frame * scoutWidth,
                        state.value.first * scoutHeight,
                        scoutWidth,
                        scoutHeight
                    )
                }.toGdxArray(), Animation.PlayMode.LOOP)
            }.toMap()
        )
    }.toMap()

    val anims = selectedItemListOf(AnimState.Idle, AnimState.Walk, AnimState.Shoot)
    val directions = selectedItemListOf(*CardinalDirection.scoutDirections.toTypedArray())


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
    val pointsCloud = PointsCloud().apply {
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
        setBoth(Input.Keys.A, "Rotate Left", { rotation = 0f }) { rotation = 5.0f }
        setBoth(Input.Keys.D, "Rotate Right", { rotation = 0f }) { rotation = -5.0f }
        setBoth(Input.Keys.W, "Extend", { extension = 0f }) { extension = .1f }
        setBoth(Input.Keys.S, "Reverse", { extension = 0f }) { extension = -.1f }
        setUp(Input.Keys.LEFT, "Previous State") { anims.previousItem() }
        setUp(Input.Keys.RIGHT, "Next State") { anims.nextItem() }
        setUp(Input.Keys.UP, "Previous State") { directions.previousItem() }
        setUp(Input.Keys.DOWN, "Next State") { directions.nextItem() }
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
        if (button == Buttons.LEFT) {
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
//            pointsCloud.rotation = pointsCloud.rotation + rotation
            scoutPosition.set(pointsCloud.worldX - scoutWidth / 2f, pointsCloud.worldY - scoutHeight / 2f)
//            batch.draw(
//                scoutAnims[anims.selectedItem]!!.animations[directions.selectedItem]!!.getKeyFrame(elapsedTime),
//                scoutPosition.x,
//                scoutPosition.y
//            )
//            shapeDrawer.filledCircle(scoutPosition, 1f, Color.GREEN)
//            scoutPosition.set(pointsCloud.worldX + scoutWidth / 2, pointsCloud.worldY + scoutHeight / 2)
//            shapeDrawer.filledCircle(scoutPosition, 1f, Color.YELLOW)
//            shapeDrawer.filledCircle(pointsCloud.position, 1f, Color.ORANGE)
//            for (point in pointsCloud.actualPoints) {
//                shapeDrawer.filledCircle(point, 1f, Color.RED)
//            }
            nodeTree.rotation = nodeTree.rotation + rotation
            nodeTree.update(delta)
            nodeTree.draw(batch, shapeDrawer, delta)


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

