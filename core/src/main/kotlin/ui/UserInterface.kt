package ui

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Queue
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.graphics.CharacterSpriteComponent
import gamestate.Player
import gamestate.Players
import ktx.scene2d.*
import physics.getComponent

class UserInterface(
    private val batch: Batch,
    debug: Boolean = true
) : IUserInterface {

    private val players get() = Players.players

    private lateinit var rootTable: KTableWidget
    private lateinit var infoBoard: KTableWidget


    override val hudViewPort = ExtendViewport(uiWidth, uiHeight, OrthographicCamera())

    @ExperimentalStdlibApi
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
        const val uiWidth = 800f
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
        for ((l, _) in playerLabels) {
            l.setText(
                """
Player $index                    
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

    @ExperimentalStdlibApi
    override fun reset() {
        setup()
    }

    val enemyInfo = Queue<String>()

    @ExperimentalStdlibApi
    private fun setup() {
        stage.clear()
        playerLabels.clear()
        setupInfo()
    }

    private val playerLabels = mutableMapOf<Label, Player>()

    @ExperimentalStdlibApi
    private fun setupInfo() {

        infoBoard = scene2d.table {
            setFillParent(false)
            bottom()
            left()
            for ((_, p) in players) {
                val l = label("PlayerLabel").cell(align = Align.left)
                row()
                playerLabels[l] = p
                table {
                    setFillParent(false)
                    left()
                    top()
                    for (i in 0 until p.lives)
                        image(
                            p.entity.getComponent<CharacterSpriteComponent>()
                                .currentAnim.keyFrames.first()) {
                            setScale(0.35f)
                            width = imageWidth * 0.35f
                        }.cell(align = Align.topLeft)
                    row().top()
                    pack()
                }
            }
        }

        rootTable = scene2d.table {
            setFillParent(true)
            bottom()
            left()
            add(infoBoard).align(Align.bottomLeft)
            pad(10f)
        }

        stage.addActor(rootTable)
    }
}