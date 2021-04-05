package ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Queue
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.graphics.CharacterSpriteComponent
import gamestate.Players
import ktx.graphics.use
import ktx.math.vec2
import physics.getComponent

class UserInterface(
    private val batch: Batch,
    debug: Boolean = true
) : IUserInterface {

    private val players get() = Players.players
    private val camera = OrthographicCamera()
    override val hudViewPort = ExtendViewport(uiWidth, uiHeight, camera)

    @ExperimentalStdlibApi
    override fun show() {
        hudViewPort.update(Gdx.graphics.width, Gdx.graphics.height, true)
    }

    override fun hide() {
        //setup clears everything, not needed.
    }

    companion object {
        private const val aspectRatio = 16 / 9
        const val uiWidth = 800f
        const val uiHeight = uiWidth * aspectRatio
    }

    @ExperimentalStdlibApi
    override fun update(delta: Float) {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.use {
            newUi.render(batch)
        }
    }

    override fun dispose() {
    }

    override fun clear() {
    }

    @ExperimentalStdlibApi
    override fun reset() {
    }

    @ExperimentalStdlibApi
    private val newUi by lazy {
        SpacedContainer(vec2(100f, 0f), vec2(20f, hudViewPort.worldHeight / 6), true).apply {
            for ((i, p) in players.values.withIndex()) {
                children.add(
                    SpacedContainer(vec2(0f, 25f), vec2()).apply {
                        children.add(
                            TextActor("Player ${i + 1}")
                        )
                        children.add(
                            BoundTextActor( {"Kills: ${p.kills}"} )
                        )
                        children.add(
                            BoundTextActor( {"Objectives: ${p.touchedObjectives.count()}"} )
                        )
                        children.add(
                            DataBoundMeter(
                                { p.health },
                                p.startingHealth,
                                50f,
                                10f
                            )
                        )
                        children.add(
                            DataBoundRepeatingTextureActor(
                                { p.lives },
                                vec2(20f, 0f),
                                p.entity
                                    .getComponent<CharacterSpriteComponent>()
                                    .currentAnim
                                    .keyFrames
                                    .first(),
                                0.5f
                            )
                        )
                    }
                )
            }
        }
    }
}

