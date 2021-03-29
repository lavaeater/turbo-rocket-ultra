package ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.ai.BehaviorComponent
import ecs.components.enemy.EnemyComponent
import factories.engine
import gamestate.Player
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import gamestate.Players
import injection.Context.inject
import ktx.ashley.allOf

class UserInterface(
    private val batch: Batch,
    debug: Boolean = false
) : IUserInterface {

    private val players get() = Players.players
    private val engine by lazy { inject<Engine>() }
    private val enemy by lazy { engine.getEntitiesFor(allOf(EnemyComponent::class, BehaviorComponent::class).get()).first() }
    private val tree by lazy { enemy.getComponent(BehaviorComponent::class.java).tree }


    private lateinit var rootTable: KTableWidget
    private lateinit var infoBoard: KTableWidget


    override val hudViewPort = ExtendViewport(uiWidth, uiHeight, OrthographicCamera())
    override fun show() {
        setup()
    }

    override fun hide() {
        //setup clears everything, not needed.
    }

    override val stage = Stage(hudViewPort, batch)
        .apply {
            isDebugAll = debug
        }

    companion object {
        private const val aspectRatio = 16 / 9
        const val uiWidth = 720f
        const val uiHeight = uiWidth * aspectRatio
    }

    @ExperimentalStdlibApi
    override fun update(delta: Float) {
        batch.projectionMatrix = stage.camera.combined

        updateInfo(delta)
        stage.act(delta)
        stage.draw()
    }

    @ExperimentalStdlibApi
    private fun updateInfo(delta: Float) {
        var index = 1
        for ((l,p) in playerLabels) {
            l.setText(
"""
Player $index                    
Health: ${p.health}
Lives: ${p.lives}
""".trimIndent()
            )
            index++
        }
        enemyLabel.setText(enemyInfo)
    }
    override fun dispose() {
        stage.dispose()
    }

    override fun clear() {
        stage.clear()
    }

    private fun setup() {
        tree.addListener(object : BehaviorTree.Listener<Entity> {
            override fun statusUpdated(task: Task<Entity>?, previousStatus: Task.Status?) {
                enemyInfo = "$task - $previousStatus"
            }

            override fun childAdded(task: Task<Entity>?, index: Int) {

            }

        })
        stage.clear()
        setupInfo()
    }

    private var enemyInfo = ""

    private val playerLabels = mutableMapOf<Label, Player>()
    private lateinit var enemyLabel: Label
    private fun setupInfo() {

        infoBoard = scene2d.table {
            for((c,p) in players) {
                val l = label("PlayerLabel")
                playerLabels[l] = p
            }
            enemyLabel= label("nuthin")
        }

        rootTable = scene2d.table {
            setFillParent(true)
            bottom()
            left()
            add(infoBoard).expand().align(Align.bottomLeft)
            pad(10f)
        }

        stage.addActor(rootTable)
    }
}