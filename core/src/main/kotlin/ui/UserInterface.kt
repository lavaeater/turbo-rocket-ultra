package ui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Queue
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

    var accumulator = 0f

    @ExperimentalStdlibApi
    private fun updateInfo(delta: Float) {
        var index = 1
        for ((l, p) in playerLabels) {
            l.setText(
                """
Player $index                    
Health: ${p.health}
Lives: ${p.lives}
""".trimIndent()
            )
            index++
        }
        accumulator += delta
        if (enemyInfo.notEmpty()) {
            currentInfo = enemyInfo.removeFirst()
        }
        enemyLabel.setText("""
            Enemies: ${engine.getEntitiesFor(allOf(EnemyComponent::class).get()).count()}
            FPS: ${Gdx.graphics.framesPerSecond}
            """)
    }

    var currentInfo = ""

    override fun dispose() {
        stage.dispose()
    }

    override fun clear() {
        stage.clear()
    }

    override fun reset() {
        setup()
    }

    val enemyInfo = Queue<String>()

    private fun setup() {
        stage.clear()
        playerLabels.clear()
        setupInfo()
    }

    private val playerLabels = mutableMapOf<Label, Player>()
    private lateinit var enemyLabel: Label
    private fun setupInfo() {

        infoBoard = scene2d.table {
            for ((_, p) in players) {
                val l = label("PlayerLabel")
                playerLabels[l] = p
            }
            enemyLabel = label("nuthin")
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