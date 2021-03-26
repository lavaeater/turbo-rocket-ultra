package ui

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.*
import gamestate.Player
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.vec2
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import physics.getComponent

class UserInterface(
    private val batch: Batch,
    debug: Boolean = false
) : IUserInterface {

    private val engine: Engine by lazy { inject() }
    private val splatterCount get() = engine.getEntitiesFor(allOf(SplatterComponent::class).get()).count()
    private val enemyCount get() = engine.getEntitiesFor(allOf(EnemyComponent::class).get()).count()
    private val objectiveCount get() = engine.getEntitiesFor(allOf(ObjectiveComponent::class).get()).count()
    private val player: Player by lazy { inject() }
    private val touchedObjectiveCount get () = player.touchedObjectives.count()
    @ExperimentalStdlibApi
    private val playerControlComponent get() = player.entity.getComponent<PlayerControlComponent>()
    private val controlMapper: ControlMapper by lazy { inject() }

    private lateinit var rootTable: KTableWidget
    private lateinit var infoBoard: KTableWidget
    private lateinit var infoLabel: Label


    override val hudViewPort = ExtendViewport(uiWidth, uiHeight, OrthographicCamera())
    override val stage = Stage(hudViewPort, batch)
        .apply {
            isDebugAll = debug
        }

    companion object {
        private const val aspectRatio = 16 / 9
        const val uiWidth = 720f
        const val uiHeight = uiWidth * aspectRatio
    }

    init {
        setup()
    }

    @ExperimentalStdlibApi
    override fun update(delta: Float) {
        batch.projectionMatrix = stage.camera.combined

        updateInfo(delta)
        stage.act(delta)
        stage.draw()
    }

//    Player Health:  ${player.health}
//    Targets Left:   ${objectiveCount - touchedObjectiveCount}
//    Splatter Count: $splatterCount
//    Enemies Left:   $enemyCount

    @ExperimentalStdlibApi
    private fun updateInfo(delta: Float) {
        infoLabel.setText(
            """
    FPS:            ${Gdx.graphics.framesPerSecond}
    Player Health:  ${player.health}
    Targets Left:   ${objectiveCount - touchedObjectiveCount}
    Splatter Count: $splatterCount
    Enemies Left:   $enemyCount
      
    """.trimIndent()
        )
    }

    private val mouseVector = vec2()
    private fun getMousePosition(): Vector2 {
        mouseVector.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        return mouseVector
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun clear() {
        stage.clear()
    }

    private fun setup() {
        stage.clear()
        setupInfo()
    }

    private fun setupInfo() {
        infoBoard = scene2d.table {

            infoLabel = label("InfoLabel")
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