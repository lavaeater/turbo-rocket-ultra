package ui

import audio.AudioPlayer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.Players
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.player.PlayerControlComponent
import ecs.systems.tileX
import ecs.systems.tileY
import injection.Context.inject
import ktx.graphics.use
import ktx.math.vec2
import ktx.scene2d.scene2d
import map.grid.GridMapManager
import physics.getComponent
import ui.simple.*


class UserInterface(
    private val batch: Batch,
    debug: Boolean
) : IUserInterface {

    private val players get() = Players.players
    private val camera = OrthographicCamera()
    override val hudViewPort = ExtendViewport(uiWidth, uiHeight, camera)
    private val mapManager by lazy { inject<GridMapManager>() }
    private val audioPlayer by lazy { inject<AudioPlayer>() }

    override fun show() {
        scene2d
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

    override fun showKillCount(count: Int) {

    }

    @ExperimentalStdlibApi
    private val newUi by lazy {
        SpacedContainer(vec2(200f, 0f), vec2(20f, hudViewPort.worldHeight / 4), true).apply {
            children.add(
                SpacedContainer(vec2(0f, 25f), vec2()).apply {
                    children.add(BoundTextActor({ audioPlayer.toString() }))
//                    children.add(BoundTextActor({ "Enemies: ${screens.CounterObject.enemyCount}" }))
//                    children.add(BoundTextActor({ "Max Enemies: ${screens.CounterObject.numberOfEnemies}" }))
//                    children.add(BoundTextActor({ "Objectives: ${screens.CounterObject.numberOfObjectives}" }))
//                    children.add(BoundTextActor({ "MapLength: ${screens.CounterObject.currentLength}" }))
//                    children.add(BoundTextActor({ "Current Level: ${screens.CounterObject.currentLevel}" }))
//                    children.add(BoundTextActor({ "Fps: ${Gdx.graphics.framesPerSecond}" }))
                }
            )
            children.add(
                SpacedContainer(vec2(0f, 25f), vec2()).apply {
                    for ((i, p) in players.values.withIndex()) {
                        val position = p.entity.getComponent<TransformComponent>().position
                        children.add(BoundTextActor({ "Tile: ${position.tileX()}:${position.tileY()}" }))
                        children.add(BoundTextActor({
                            "CanBuild: ${
                                mapManager.canWeBuildAt(
                                    position.tileX(),
                                    position.tileY()
                                )
                            }"
                        }))
                    }
                }
            )
            for ((i, p) in players.values.withIndex()) {
                children.add(
                    SpacedContainer(vec2(0f, 25f), vec2()).apply {
                        children.add(
                            TextActor("Player ${i + 1}")
                        )
                        children.add(
                            BoundTextActor({ "Kills: ${p.kills}" })
                        )
                        children.add(
                            BoundTextActor({ "Objectives: ${p.touchedObjectives.count()}" })
                        )
                        children.add(
                            BoundTextActor({ "Score: ${p.score}" })
                        )
                        children.add(
                            BoundTextActor({ "${p.currentWeapon}: ${p.ammoLeft}/${p.totalAmmo}" })
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
                                (p.entity
                                    .getComponent<AnimatedCharacterComponent>())
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

