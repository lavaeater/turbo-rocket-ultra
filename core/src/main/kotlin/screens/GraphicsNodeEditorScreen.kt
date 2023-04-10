package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.systems.graphics.GameConstants
import extensions.normToWorld
import extensions.worldToNorm
import gamestate.GameEvent
import gamestate.GameState
import ktx.graphics.use
import map.snake.bottom
import map.snake.left
import map.snake.right
import map.snake.top
import screens.basic.BasicScreen
import screens.command.command
import screens.ui.GraphicsNode
import screens.ui.MousePosition
import space.earlygrey.shapedrawer.ShapeDrawer
import statemachine.StateMachine
import tru.Assets
import kotlin.math.absoluteValue

class GraphicsNodeEditorScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private var drawPointerBall = false
    private val normalCommandMap = command("Normal") {
    }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    private val stage by lazy {
        val aStage = Stage(ExtendViewport(1600f, 1200f, OrthographicCamera()), batch)
    }
    private var currentlySelectedNode: GraphicsNode? = GraphicsNode()
    private var nodeUnderMouse: GraphicsNode? = null
    private val graphicsNodes = mutableListOf(currentlySelectedNode!!)
    private val allNodes = mutableListOf(currentlySelectedNode!!)

    /*
    What is a keyframe? It could simply be defined as the movement of something to another place

    But how are things moving?

    These are questions I have yet to answer
     */


    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        drawPointerBall = button == Input.Buttons.LEFT
        return true
    }

    fun getNodeAt(screenX: Int, screenY: Int): GraphicsNode? {
        val normPos = MousePosition.toWorld(screenX, screenY).worldToNorm()
        return allNodes.firstOrNull { it.globalPosition.dst(normPos) < 0.25f }
    }

    fun checkNodeUnderMouse() {
        val normPos = MousePosition.toWorld()
        nodeUnderMouse = allNodes.firstOrNull { it.globalPosition.normToWorld().dst(normPos) < 0.25f }
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        drawPointerBall = false
        if (button == Input.Buttons.LEFT) {
            if(nodeUnderMouse != null)
                currentlySelectedNode = nodeUnderMouse
            else {
                if (currentlySelectedNode == null)
                    addNodeAt(screenX, screenY)
                else {
                    addChildNodeAt(screenX, screenY, currentlySelectedNode!!)
                }
            }
        } else if(button == Input.Buttons.RIGHT) {
            currentlySelectedNode = null
        }
        return true
    }

    private fun addChildNodeAt(screenX: Int, screenY: Int, parent: GraphicsNode) {
        val p = MousePosition.toWorld(screenX, screenY).worldToNorm()
        allNodes.add(parent.addChild(p.x, p.y))
    }

    private fun addNodeAt(screenX: Int, screenY: Int) {
        val p = MousePosition.toWorld(screenX, screenY).worldToNorm()
        val node = GraphicsNode(p.x, p.y)
        graphicsNodes.add(node)
        allNodes.add(node)
        currentlySelectedNode = node
    }

    private lateinit var drawArea: Rectangle
    private var drawAreaWidth: Float = 0f
    private var drawAreaHeight: Float = 0f

    override fun show() {
        super.show()
        drawAreaWidth = GameConstants.GAME_HEIGHT - 5f
        drawAreaHeight = GameConstants.GAME_HEIGHT - 5f
        drawArea = Rectangle(
            2.5f,
            2.5f,
            drawAreaWidth,
            drawAreaHeight
        )
    }



    override fun render(delta: Float) {
        super.render(delta)
        checkNodeUnderMouse()
        val mousePosition = MousePosition.toWorld()

        shapeDrawer.batch.use {

            val vertYes = allNodes.any { (it.globalPosition.normToWorld().x - mousePosition.x).absoluteValue < 0.05f }
            val horiYes= allNodes.any { (it.globalPosition.normToWorld().y - mousePosition.y).absoluteValue < 0.05f }

            shapeDrawer.line(mousePosition.x, drawArea.top(), mousePosition.x, drawArea.bottom(), if(vertYes) Color.BLUE else Color.WHITE, 0.1f)
            shapeDrawer.line(drawArea.left(), mousePosition.y, drawArea.right(), mousePosition.y, if(horiYes) Color.BLUE else Color.WHITE, 0.1f)



            shapeDrawer.rectangle(drawArea, Color.WHITE, 0.1f)
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
            if(nodeUnderMouse == graphicsNode) Color.RED else if(currentlySelectedNode == graphicsNode) Color.GREEN else Color.BLUE
        )
        for (childNode in graphicsNode.children) {
            shapeDrawer.line(graphicsNode.globalPosition.normToWorld(), childNode.globalPosition.normToWorld(),
                Color.BLACK, .1f)
            drawNode(shapeDrawer, childNode)
        }
    }
}