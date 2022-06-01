package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.systems.graphics.GameConstants.GAME_HEIGHT
import ecs.systems.graphics.GameConstants.GAME_WIDTH
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import ktx.graphics.use
import ktx.math.plus
import ktx.math.vec2
import ktx.math.vec3
import space.earlygrey.shapedrawer.ShapeDrawer
import statemachine.StateMachine
import tru.Assets

object MousePosition {
    private val mousePosition3D = vec3()
    private val mousePosition2D = vec2()
    private val camera by lazy { inject<OrthographicCamera>() }
    fun toWorld(screenX: Int, screenY: Int): Vector2 {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition2D.set(mousePosition3D.x, mousePosition3D.y)
        return mousePosition2D
    }

    fun toWorld(): Vector2 {
        return toWorld(Gdx.input.x, Gdx.input.y)
    }

    const val margin = 2.5f
    const val minX =  0f + margin
    const val maxX = GAME_HEIGHT - margin
    const val maxY = GAME_HEIGHT - margin
    const val minY = 0f + margin
}

fun Vector2.worldToNorm(): Vector2 {
    return this.worldToNorm(MousePosition.minX, MousePosition.maxX, MousePosition.minY, MousePosition.maxY)
}

fun Vector2.worldToNorm(minX: Float, maxX: Float, minY: Float, maxY: Float): Vector2 {
    return vec2(MathUtils.norm(minX, maxX, x), MathUtils.norm(minY, maxY, y))
}

fun Vector2.normToWorld():Vector2 {
    return normToWorld(MousePosition.minX, MousePosition.maxX, MousePosition.minY, MousePosition.maxY)
}

fun Vector2.normToWorld(minX: Float, maxX: Float, minY: Float, maxY: Float): Vector2 {
    return vec2(MathUtils.lerp(minX, maxX, x), MathUtils.lerp(minY, maxY, y))
}

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private var drawPointerBall = false
    private val normalCommandMap = command("Normal") {
    }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    private val stage by lazy {
        val aStage = Stage(ExtendViewport(1600f, 1200f, OrthographicCamera()), batch)
    }
    private var currentlySelectedNode: GraphicsNode? = GraphicsNode()
    private val graphicsNodes = mutableListOf(currentlySelectedNode!!)

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        drawPointerBall = button == Buttons.LEFT
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        drawPointerBall = false
        if (button == Buttons.LEFT) {
            if(currentlySelectedNode == null)
                addNodeAt(screenX, screenY)
            else {
                addChildNodeAt(screenX, screenY, currentlySelectedNode!!)
            }
        }
        return true
    }

    private fun addChildNodeAt(screenX: Int, screenY: Int, parent: GraphicsNode) {
        val p = MousePosition.toWorld(screenX, screenY).worldToNorm()
        parent.addChild(p.x, p.y)
    }

    private fun addNodeAt(screenX: Int, screenY: Int) {
        val p = MousePosition.toWorld(screenX, screenY).worldToNorm()
        graphicsNodes.add(GraphicsNode(p.x,p.y))
    }

    private lateinit var drawArea: Rectangle
    private var drawAreaWidth: Float = 0f
    private var drawAreaHeight: Float = 0f

    override fun show() {
        super.show()
        drawAreaWidth = GAME_HEIGHT - 5f
        drawAreaHeight = GAME_HEIGHT - 5f
        drawArea = Rectangle(
            2.5f,
            2.5f,
            drawAreaWidth,
            drawAreaHeight
        )
    }

    override fun render(delta: Float) {
        super.render(delta)
        shapeDrawer.batch.use {
            shapeDrawer.rectangle(drawArea, Color.WHITE, 0.5f)
            if (drawPointerBall) {
                shapeDrawer.filledCircle(MousePosition.toWorld(), .5f, Color.GREEN)
            }
            //Draw with children first strategy?
            for (node in graphicsNodes) {
                drawNode(shapeDrawer, node)
            }
        }
    }

    fun drawNode(shapeDrawer: ShapeDrawer, graphicsNode: GraphicsNode) {
        shapeDrawer.filledCircle(
            graphicsNode.globalPosition.normToWorld(),
            .5f,
            if(currentlySelectedNode == graphicsNode) Color.GREEN else Color.BLUE
        )
        for (childNode in graphicsNode.children) {
            shapeDrawer.line(graphicsNode.globalPosition.normToWorld(), childNode.globalPosition.normToWorld(), Color.BLACK, .1f)
            drawNode(shapeDrawer, childNode)
        }
    }
}

class GraphicsNode(localX: Float = 0.5f, localY: Float = 0.5f, var rotation: Float = 0f, var parent: GraphicsNode? = null) {
    var localX: Float
        set(value) {
            localPosition.x = value
        }
        get() = localPosition.x
    var localY: Float
        set(value) {
            localPosition.y = value
        }
        get() = localPosition.y

    val localPosition = vec2(localX, localY)
    val globalPosition: Vector2
        get() {
            if (parent == null) {
                return localPosition
            } else {
                /*
                Intriguing

                What does this even mean?
                The position of the child node is relative to the parent node.
                If the parent rotates, this affects where the child is henceforth.
                Ah, I know. The localX and localY are what we ADD to the parent
                location to get global location. Tha means that if we rotate the parent, we
                will automagically get the children rotated, right? So the child position IS
                basically the vector from parent to child!
                And this distance is always normalized since we calculate it?
                 */
                return parent!!.globalPosition + localPosition
            }
        }
    val children = mutableListOf<GraphicsNode>()
    fun addChild(globalX: Float, globalY: Float) : GraphicsNode {
        val lp = vec2(globalX, globalY).sub(globalPosition)
        val newChild = GraphicsNode(lp.x, lp.y,0f, this)
        children.add(newChild)
        return newChild
    }
}
