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
import input.ControlMapper
import ktx.ashley.allOf
import ktx.math.vec2
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import physics.getComponent
import screens.Players

class UserInterface(
    private val batch: Batch,
    debug: Boolean = false
) : IUserInterface {

    private val players get() = Players.players

    private lateinit var rootTable: KTableWidget
    private lateinit var infoBoard: KTableWidget


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

    @ExperimentalStdlibApi
    private fun updateInfo(delta: Float) {
        var index = 1
        for ((l,p) in playerLabels) {
            l.setText(
"""
Player $index                    
Health: ${p.health}
""".trimIndent()
            )
            index++
        }
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

    private val playerLabels = mutableMapOf<Label, Player>()
    private fun setupInfo() {
        infoBoard = scene2d.table {
            for((c,p) in players) {
                playerLabels[label("PlayerLabel")] = p
            }
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