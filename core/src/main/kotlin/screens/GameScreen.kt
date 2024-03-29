package screens

import ai.pathfinding.TileGraph
import audio.AudioPlayer
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.strongjoshua.console.GUIConsole
import data.Players
import eater.injection.InjectionContext.Companion.inject
import eater.physics.body
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.graphics.CameraFollowComponent
import ecs.components.player.PlayerComponent
import ecs.systems.graphics.CameraUpdateSystem
import ecs.systems.graphics.GameConstants.MAX_ENEMIES
import ecs.systems.graphics.RenderMiniMapSystem
import ecs.systems.graphics.RenderSystem
import ecs.systems.input.GamepadInputSystem
import ecs.systems.input.KeyboardInputSystem
import ecs.systems.player.GameOverSystem
import factories.addUiThing
import factories.enemy
import factories.player
import gamestate.GameEvent
import gamestate.GameState
import input.KeyboardControl
import ktx.app.KtxScreen
import ktx.ashley.allOf
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.log.debug
import map.grid.*
import map.snake.randomPoint
import physics.*
import statemachine.StateMachine
import tru.Assets
import eater.turbofacts.Factoids
import eater.turbofacts.TurboFactsOfTheWorld
import eater.turbofacts.TurboStoryManager
import eater.turbofacts.factsOfTheWorld
import factories.enemy
import ui.IUserInterface
import kotlin.math.pow
import kotlin.math.roundToInt


class GameScreen(private val gameState: StateMachine<GameState, GameEvent>) : KtxScreen {

    private var firstRun = true
    private val camera: OrthographicCamera by lazy { inject() }
    private val viewPort: ExtendViewport by lazy { inject() }
    private val engine: Engine by lazy { inject() }
    private val world: World by lazy { inject() }
    private val batch: PolygonSpriteBatch by lazy { inject() }
    private val ui: IUserInterface by lazy { inject() }
    private val audioPlayer: AudioPlayer by lazy { inject() }
    private val storyManager: TurboStoryManager by lazy { inject() }
    private val factsOfTheWorld: TurboFactsOfTheWorld by lazy { factsOfTheWorld() }
    private val console by lazy { inject<GUIConsole>() }
    private val vfxManager by lazy { inject<VfxManager>() }
    private var running = true

    override fun show() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        initializeIfNeeded()
        if (running) {
            camera.setToOrtho(true, viewPort.maxWorldWidth, viewPort.maxWorldHeight)
            Gdx.input.inputProcessor = engine.getSystem(KeyboardInputSystem::class.java)
            Controllers.addListener(engine.getSystem(GamepadInputSystem::class.java))

            engine.getSystem<CameraUpdateSystem>().reset()
            engine.removeAllEntities()
            CounterObject.currentLevel = 1

            for (system in engine.systems) {
                system.setProcessing(true)
            }
            generateMap(CounterObject.currentLevel)

            if (Players.players.keys.any { it.isKeyboard }) {
                engine.getSystem<KeyboardInputSystem>().setProcessing(true)
            } else {
                engine.getSystem<KeyboardInputSystem>().setProcessing(false)
            }

            ui.reset()
            ui.show()

            Assets.music.first().isLooping = true
            Assets.music.first().play()

            storyManager.activate()
        }
    }

    private fun loadMap(data: MapData): Pair<Map<Coordinate, GridMapSection>, TileGraph> {
        debug { "Loading ${data.name}" }
        return GridMapGenerator.generateFromMapFile(data)
    }

    val red = 164f / 255f
    val green = 174f / 255f
    val blue = 118f / 255f

    override fun render(delta: Float) {

        //Update viewport and camera here and nowhere else...

        updatePhysics(delta)

        engine.update(delta)
        ui.update(delta)
        audioPlayer.update(delta)
        turboStoryManager.checkIfNeeded()
    }

    private val turboStoryManager by lazy { inject<TurboStoryManager>() }

    private val velIters = 8
    private val posIters = 3
    private val timeStep = 1 / 60f

    var accumulator = 0f

    private fun updatePhysics(delta: Float) {
        val ourTime = delta.coerceAtMost(timeStep * 2)
        accumulator += ourTime
        while (accumulator > timeStep) {
            world.step(timeStep, velIters, posIters)
            accumulator -= ourTime
        }
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
        vfxManager.resize(width, height)
    }

    override fun pause() {
        for (system in engine.systems)
            system.setProcessing(false)

        //Continue to render, though  gaah
        engine.getSystem<RenderSystem>().setProcessing(true)
        engine.getSystem<RenderMiniMapSystem>().setProcessing(true)

        ui.pause()

        running = false
    }

    override fun resume() {
        if (factsOfTheWorld.getBoolean(Factoids.GotoNextLevel))
            nextLevel()
        Gdx.input.inputProcessor = engine.getSystem(KeyboardInputSystem::class.java)
        Controllers.addListener(engine.getSystem(GamepadInputSystem::class.java))
        for (system in engine.systems)
            system.setProcessing(true)

        ui.resume()
        running = true

    }

    override fun hide() {
        ui.hide()
        Gdx.input.inputProcessor = null
        Controllers.removeListener(engine.getSystem(GamepadInputSystem::class.java))
        for (system in engine.systems)
            system.setProcessing(false)
    }

    private fun initializeIfNeeded() {
        if (firstRun) {
            engine.addSystem(GameOverSystem(gameState))
            firstRun = false
        }
    }

    private fun addPlayers() {
        val startBounds = mapManager.gridMap.values.first { it.startSection }.innerBounds
        for ((controlComponent, player) in Players.players) {
            if (player.isAiPlayer) {
                val enemy = enemy(startBounds.randomPoint(), false) {
                    with<CameraFollowComponent>()
                    with<PlayerComponent> {
                        this.player = player
                    }
                    with<KeyboardControl> {1

                    }
                }
                player.entity = enemy
                player.body = enemy.body()
            } else
                player(player, controlComponent, startBounds.randomPoint(), false)
        }
    }

    private fun movePlayersToStart() {
        try {
            val startBounds = mapManager.gridMap.values.first { it.startSection }.innerBounds
            val players = engine.getEntitiesFor(allOf(PlayerComponent::class).get())
            if (players.none()) {
                addPlayers()
            } else
                for (player in players) {
                    val body = AshleyMappers.body.get(player).body!!
                    body.setTransform(startBounds.randomPoint(), body.angle)
                }
        } catch (e: Exception) {
            ktx.log.error { "Couldn't move player to start area, check map to see if there is a start area (s) ${e.message}" }
        }
    }

    private fun nextLevel() {
        /*
        Needs to load the stories for the level, they might all
        need to be loaded again, which is probably better.
         */


        for (player in Players.players.values) {
            player.touchedObjectives.clear()
        }

        CounterObject.currentLevel++
        generateMap(CounterObject.currentLevel)
        if (Players.players.keys.any { it.isKeyboard }) {
            engine.getSystem<KeyboardInputSystem>().setProcessing(true)
        } else {
            engine.getSystem<KeyboardInputSystem>().setProcessing(false)
        }
        storyManager.activate()
    }

    private val mapManager by lazy { inject<GridMapManager>() }

    private fun clearAllButPlayers() {
        //Pause rendering RIGHT NOW
        engine.getSystem<RenderSystem>().setProcessing(false)
        engine.getSystem<RenderMiniMapSystem>().setProcessing(false)
        for (entity in engine.entities) {
            if (!entity.isPlayer() && !entity.hasWeapon()) {
                if (entity.hasBody()) {
                    val body = entity.body()
                    world.destroyBody(body)
                }
                entity.removeAll()
                engine.removeEntity(entity)
            }
        }
        //Resume rendering
        //Continue to render, though  gaah
        engine.getSystem<RenderSystem>().setProcessing(true)
        engine.getSystem<RenderMiniMapSystem>().setProcessing(true)
    }

    private fun generateMap(level: Int) {
        clearAllButPlayers()
        CounterObject.enemyCount = 0

        //For debuggin we will swarm with enemies
        CounterObject.maxEnemies = (8f.pow(CounterObject.currentLevel).roundToInt() * 2).coerceAtMost(MAX_ENEMIES)
        CounterObject.maxSpawnedEnemies = CounterObject.maxEnemies * 2

        val map = when {
            level < MapList.mapFileNames.size -> loadMap(MapList.mapFiles[level - 1])
            else -> GridMapGenerator.generate(CounterObject.currentLength, level)
        }
        mapManager.removeLights(mapManager.gridMap)
        mapManager.gridMap = map.first
        mapManager.sectionGraph = map.second
        CounterObject.numberOfObjectives = engine.getEntitiesFor(allOf(ObjectiveComponent::class).get()).count()
        movePlayersToStart()
    }

    override fun dispose() {
        vfxManager.dispose()
        inject<List<ChainVfxEffect>>().forEach { it.dispose() }
    }
}

