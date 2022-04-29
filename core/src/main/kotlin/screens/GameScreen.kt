package screens

import audio.AudioPlayer
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.BodyComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySpawnerComponent
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerComponent
import ecs.systems.graphics.CameraUpdateSystem
import ecs.systems.graphics.RenderMiniMapSystem
import ecs.systems.graphics.RenderSystem
import ecs.systems.input.GamepadInputSystem
import injection.Context.inject
import ecs.systems.input.KeyboardInputSystem
import ecs.systems.player.GameOverSystem
import factories.*
import gamestate.GameEvent
import gamestate.GameState
import gamestate.Players
import ktx.app.KtxScreen
import ktx.ashley.allOf
import ktx.ashley.getSystem
import ktx.ashley.mapperFor
import ktx.ashley.remove
import ktx.math.random
import ktx.math.vec2
import map.grid.GridMapGenerator
import map.grid.GridMapManager
import map.snake.*
import physics.getComponent
import statemachine.StateMachine
import ui.IUserInterface
import kotlin.math.pow
import kotlin.math.roundToInt


class GameScreen(private val gameState: StateMachine<GameState, GameEvent>) : KtxScreen {

    companion object {
        const val ENEMY_DENSITY = .1f
        const val SHOT_DENSITY = .01f
        const val SHIP_DENSITY = .1f
        const val PLAYER_DENSITY = 1f
        const val CAR_DENSITY = .3f
        const val SHIP_LINEAR_DAMPING = 20f
        const val SHIP_ANGULAR_DAMPING = 20f
        const val MAX_ENEMIES = 700

        const val GAMEWIDTH = 64f
        const val GAMEHEIGHT = 48f
    }

    private var firstRun = true
    private val camera: OrthographicCamera by lazy { inject() }
    private val viewPort: ExtendViewport by lazy { inject() }
    private val engine: Engine by lazy { inject() }
    private val world: World by lazy { inject() }
    private val batch: PolygonSpriteBatch by lazy { inject() }
    private val ui: IUserInterface by lazy { inject() }
    private val audioPlayer: AudioPlayer by lazy { inject() }
    private val transformMapper = mapperFor<TransformComponent>()

    override fun show() {
        initializeIfNeeded()
        camera.setToOrtho(true, viewPort.maxWorldWidth, viewPort.maxWorldHeight)
        Gdx.input.inputProcessor = engine.getSystem(KeyboardInputSystem::class.java)
        Controllers.addListener(engine.getSystem(GamepadInputSystem::class.java))

        engine.getSystem<CameraUpdateSystem>().reset()
        engine.removeAllEntities()
        currentLevel = 1

        for (system in engine.systems) {
            system.setProcessing(true)
        }
        generateMap()
        addPlayers()
        addEnemies()

        addTower()
        ui.reset()
        ui.show()
    }

    private fun addEnemies() {
        enemy(11f, 11f)

        objective(0f, 0f)

        val emitter = obstacle(10f, 10f)
        emitter.add(engine.createComponent(EnemySpawnerComponent::class.java))
    }

    private fun addTower() {
        //tower(vec2(-2f,2f))
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
        engine.update(delta)
        ui.update(delta)
        audioPlayer.update(delta)
        if (Players.players.values.sumOf { it.touchedObjectives.count() } == numberOfObjectives) //Add check if we killed all enemies
            nextLevel()
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
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
        val xRange = startBounds.left()..startBounds.right()
        val yRange = startBounds.bottom()..startBounds.top()
        for ((controlComponent, player) in Players.players) {
            player(player, controlComponent, startBounds.randomPoint())
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun movePlayersToStart() {
        val startBounds = mapManager.gridMap.values.first { it.startSection }.innerBounds
        val players = engine.getEntitiesFor(allOf(PlayerComponent::class).get())
        for(player in players) {
            val body = player.getComponent<BodyComponent>().body
            body.setTransform(startBounds.randomPoint(), body.angle)
        }
    }

    var currentLevel = 1
    var numberOfObjectives = 1
    var numberOfEnemies = 1
    val randomFactor = -500f..500f
    val enemyRandomFactor = -15f..15f

    fun nextLevel() {
        for (player in Players.players.values) {
            player.touchedObjectives.clear()
        }
        currentLevel++
        generateMap()
    }

    private val bodyMapper = mapperFor<BodyComponent>()
    private val mapManager by lazy { inject<GridMapManager>() }
    private fun generateMap() {
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

        for (enemy in engine.getEntitiesFor(allOf(EnemyComponent::class).get())) {
            val bodyComponent = bodyMapper.get(enemy)
            world.destroyBody(bodyComponent.body)
            enemy.remove<BodyComponent>()
        }

        engine.removeAllEntities(allOf(EnemyComponent::class).get())

        for (objective in engine.getEntitiesFor(allOf(ObjectiveComponent::class).get())) {
            val bodyComponent = bodyMapper.get(objective)
            world.destroyBody(bodyComponent.body)
            objective.remove<BodyComponent>()
        }
        engine.removeAllEntities(allOf(ObjectiveComponent::class).get())

        numberOfEnemies = (2f.pow(currentLevel).roundToInt() * 2).coerceAtMost(MAX_ENEMIES)
        numberOfObjectives = 2f.pow(currentLevel).roundToInt()

        mapManager.gridMap = GridMapGenerator.generate(currentLevel * 16)
        movePlayersToStart()


//        var randomAngle = (0f..360f)
//        val startVector = Vector2.X.cpy().scl(100f).setAngleDeg(randomAngle.random())
//

//
//
//
//        for (x in 1..25)
//            for (y in 1..25) {
//                obstacle(x * randomFactor.random(), y * randomFactor.random())
//            }
//
//        val position = transformMapper.get(Players.players.values.first().entity).position.cpy()
//
//

    }

    override fun dispose() {
        // Destroy screen's assets here.
    }
}
