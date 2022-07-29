package screens

import ai.Tree
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import com.badlogic.gdx.ai.btree.branch.Parallel
import com.badlogic.gdx.ai.btree.branch.Selector
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed
import com.badlogic.gdx.ai.btree.decorator.Invert
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils.acos
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import gamestate.GameEvent
import gamestate.GameState
import ktx.math.vec2
import ktx.scene2d.*
import statemachine.StateMachine
import tru.Assets

/*
I think custom classes is the way to go.

Take complete control. Re-build the entire behavior tree when
removing children, if necessary, keep separate view model of children.

As it stands now, if we cannot remove children, there is no point.
 */



fun label(text: String, skin: Skin = Scene2DSkin.defaultSkin, labelStyleName: String = defaultStyle): Actor {
    return Label(text,skin, labelStyleName)
}


/**
 * Editor / Displayer of Behavior Trees
 */
class BehaviorTreeViewScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    private val normalCommandMap = command("Normal") {
    }
    private val testTree = Tree.nowWithAttacks()

    private val stage by lazy {
        val aStage = Stage(ExtendViewport(1600f, 1200f, OrthographicCamera()), batch)
//        val aTree = com.badlogic.gdx.scenes.scene2d.ui.Tree<TaskNode<Entity>, Task<Entity>>(Scene2DSkin.defaultSkin, defaultStyle)
//        aTree.addActor(label("Tree"))
//        aTree.add(TaskNode.buildNodeForTask(testTree))

        aStage.actors {
            tree {
                setPadding(20f)
                label("Tree") { node ->
                    node.isExpanded = true
                    BehaviorTreeViewBuilder.nodeForTask(node, testTree)
                }
                setPosition(100f, stage.height - 100f)
            }
        }
        Gdx.input.inputProcessor = aStage
        aStage
    }


    override val camera = OrthographicCamera().apply {
        setToOrtho(false)
    }
    override val viewport = FitViewport(32f, 32f, camera)
    val shapeDrawer by lazy { Assets.shapeDrawer }
    private val cameraMove = vec2()
    private var currentControlMap: CommandMap = normalCommandMap
    private val zoomFactor = 0.05f
    private var cameraZoom: Float = 0f

    override fun render(delta: Float) {
        camera.position.x += cameraMove.x
        camera.position.y += cameraMove.y
        camera.zoom += zoomFactor * cameraZoom
        super.render(delta)
        stage.act()
        stage.draw()
    }

    override fun keyDown(keycode: Int): Boolean {
        return currentControlMap.execute(keycode, KeyPress.Down)
    }

    override fun keyUp(keycode: Int): Boolean {
        return currentControlMap.execute(keycode, KeyPress.Up)
    }
}

