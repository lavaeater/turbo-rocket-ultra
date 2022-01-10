package screens

import ai.pathfinding.TileGraph
import audio.AudioPlayer
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.strongjoshua.console.GUIConsole
import data.Players
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.player.PlayerComponent
import ecs.systems.graphics.CameraUpdateSystem
import ecs.systems.graphics.GameConstants.MAX_ENEMIES
import ecs.systems.graphics.RenderMiniMapSystem
import ecs.systems.graphics.RenderSystem
import ecs.systems.input.GamepadInputSystem
import ecs.systems.input.KeyboardInputSystem
import ecs.systems.player.GameOverSystem
import factories.player
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import ktx.app.KtxScreen
import ktx.ashley.allOf
import ktx.ashley.getSystem
import map.grid.*
import map.snake.randomPoint
import physics.AshleyMappers
import statemachine.StateMachine
import story.FactsOfTheWorld
import story.StoryHelper
import story.StoryManager
import story.fact.Facts
import tru.Assets
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
    private val storyManager: StoryManager by lazy { inject() }
    private val factsOfTheWorld: FactsOfTheWorld by lazy { inject() }
    private val console by lazy { inject<GUIConsole>() }
    private val vfxManager by lazy { inject<VfxManager>() }

    override fun show() {
        initializeIfNeeded()
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
        addPlayers()

        if(Players.players.keys.any { it.isKeyboard }) {
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

    private fun loadMapZero() : Pair<Map<Coordinate, GridMapSection>, TileGraph> {
        CounterObject.maxEnemies = 0
        CounterObject.maxSpawnedEnemies = 0
        return GridMapGenerator.generateFromDefintion(TextGridMapDefinition.levelZero, true)
    }

    private fun loadMapOne() : Pair<Map<Coordinate, GridMapSection>, TileGraph> {
        CounterObject.maxEnemies = 1024
        CounterObject.maxSpawnedEnemies = 1024

        storyManager.addStory(StoryHelper.enemyKillCountStory)
        return GridMapGenerator.generateFromDefintion(TextGridMapDefinition.levelOne)
    }

    private fun loadMapTwo(): Pair<Map<Coordinate, GridMapSection>, TileGraph>  {
        CounterObject.maxEnemies = 100
        CounterObject.maxSpawnedEnemies= 1024
        storyManager.addStory(StoryHelper.basicStory)
        return GridMapGenerator.generateFromDefintion(TextGridMapDefinition.levelTwo)
    }

    private fun loadMapThree(): Pair<Map<Coordinate, GridMapSection>, TileGraph>  {
        CounterObject.maxEnemies = 300
        CounterObject.maxSpawnedEnemies= 1024

        storyManager.addStory(StoryHelper.basicStory)
        return GridMapGenerator.generateFromDefintion(TextGridMapDefinition.levelThree)
    }
    private fun loadMapFour(): Pair<Map<Coordinate, GridMapSection>, TileGraph>  {
        CounterObject.maxEnemies = 512
        CounterObject.maxSpawnedEnemies= 1024

        storyManager.addStory(StoryHelper.basicStory)
        return GridMapGenerator.generateFromDefintion(TextGridMapDefinition.levelFour)
    }
    /*
    4E4048
A4AE76
F1E1B5
D1B67A
82503A
    #a4ae76
     */

    val red = 164f / 255f
    val green = 174f / 255f
    val blue = 118f / 255f

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        //Update viewport and camera here and nowhere else...
        camera.update(true)
        batch.projectionMatrix = camera.combined
        updatePhysics(delta)

        engine.update(delta)
        ui.update(delta)
        audioPlayer.update(delta)
        storyManager.checkStories()

        if (factsOfTheWorld.getBooleanFact(Facts.LevelComplete).value)
            nextLevel()
    }

    private val velIters = 8
    private val posIters = 3
    private val timeStep = 1/60f

    var accumulator = 0f

    private fun updatePhysics(delta:Float) {
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

        //Continue to render, though
        engine.getSystem<RenderSystem>().setProcessing(true)
        engine.getSystem<RenderMiniMapSystem>().setProcessing(true)
    }

    override fun resume() {
        Gdx.input.inputProcessor = engine.getSystem(KeyboardInputSystem::class.java)
        Controllers.addListener(engine.getSystem(GamepadInputSystem::class.java))
        for (system in engine.systems)
            system.setProcessing(true)
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
            player(player, controlComponent, startBounds.randomPoint(), false)
        }
    }

    private fun movePlayersToStart() {
        val startBounds = mapManager.gridMap.values.first { it.startSection }.innerBounds
        val players = engine.getEntitiesFor(allOf(PlayerComponent::class).get())
        for (player in players) {
            val body = AshleyMappers.body.get(player).body!!
            body.setTransform(startBounds.randomPoint(), body.angle)
        }
    }

    private fun nextLevel() {
        for (player in Players.players.values) {
            player.touchedObjectives.clear()
        }
//        factsOfTheWorld.stateBoolFact(Facts.BossIsDead, false)
//        factsOfTheWorld.stateBoolFact(Facts.AllObjectivesAreTouched, false)
//        factsOfTheWorld.stateBoolFact(Facts.LevelComplete, false)
//        factsOfTheWorld.stateBoolFact(Facts.ShowEnemyKillCount, false)


        CounterObject.currentLevel++
        generateMap(CounterObject.currentLevel)
        if(Players.players.keys.any { it.isKeyboard }) {
            engine.getSystem<KeyboardInputSystem>().setProcessing(true)
        } else {
            engine.getSystem<KeyboardInputSystem>().setProcessing(false)
        }
        storyManager.activate()
    }

    private val mapManager by lazy { inject<GridMapManager>() }

    private fun clearAllButPlayers() {
        for(entity in engine.entities) {
            if(!AshleyMappers.playerControl.has(entity)) {
                if(AshleyMappers.body.has(entity)) {
                    val body = AshleyMappers.body.get(entity).body!!
                    world.destroyBody(body)
                }
                engine.removeEntity(entity)
            }
        }
    }

    private fun generateMap(level: Int) {
        /*
        We start the game with a map already generated. But when, how, will we create
        all the entities and stuff? The map should and must be generated HERE, not
        anywhere else.

        And what do we do with the crazy fucking thing that the map makes no sense and
        is all over the place? Damnit. That thing actually came back to bite me. Darn.

        The absolutely easiest way to do it is to simply make the map geometrically consistent.

        BORING!

        Now add a goddamned  light
         */

        clearAllButPlayers()
        CounterObject.enemyCount = 0

        //For debuggin we will swarm with enemies
        CounterObject.maxEnemies =  (8f.pow(CounterObject.currentLevel).roundToInt() * 2).coerceAtMost(MAX_ENEMIES)
        CounterObject.maxSpawnedEnemies = CounterObject.maxEnemies * 2
        val map = when(level) {
            1 -> loadMapZero()//loadMapOne()
            2 -> loadMapTwo()
            3 -> loadMapThree()
            4 -> loadMapFour()
            else -> GridMapGenerator.generate(CounterObject.currentLength, level)
        }
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

