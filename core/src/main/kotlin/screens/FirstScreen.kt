package screens

import audio.AudioPlayer
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.BodyComponent
import ecs.components.EnemyComponent
import ecs.components.ObjectiveComponent
import ecs.components.TransformComponent
import factories.enemy
import factories.objective
import factories.obstacle
import gamestate.Player
import injection.Context.inject
import input.InputAdapter
import ktx.app.KtxScreen
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.ashley.remove
import ktx.math.random
import tru.Assets
import ui.IUserInterface
import kotlin.math.pow
import kotlin.math.roundToInt


class FirstScreen : KtxScreen {

    companion object {
        const val ENEMY_DENSITY = .1f
        const val SHOT_DENSITY = .01f
        const val SHIP_DENSITY = .1f
        const val PLAYER_DENSITY = 1f
        const val CAR_DENSITY = .3f
        const val SHIP_LINEAR_DAMPING = 20f
        const val SHIP_ANGULAR_DAMPING = 20f

        const val GAMEWIDTH = 64f
        const val GAMEHEIGHT = 48f
    }

    private var needsInit = true

    private val camera: OrthographicCamera by lazy { inject() }
    private val viewPort: ExtendViewport by lazy { inject() }
    private val engine: Engine by lazy { inject() }
    private val world: World by lazy { inject() }
    private val batch: PolygonSpriteBatch by lazy { inject() }
    private val ui: IUserInterface by lazy { inject() }
    private val audioPlayer: AudioPlayer by lazy { inject() }
    private val player: Player by lazy { inject() }
    private val transformMapper = mapperFor<TransformComponent>()

    override fun show() {
        initializeIfNeeded()
    }

    private lateinit var inputAdapter:InputAdapter
    private fun setupInput() {
        inputAdapter = InputAdapter()
        Gdx.input.inputProcessor = inputAdapter
        Controllers.addListener(inputAdapter)
    }


    var currentLevel = 0
    var numberOfObjectives = 1
    var numberOfEnemies = 1
    val randomFactor = -500f..500f
    val enemyRandomFactor = -15f..15f

    fun nextLevel() {
        currentLevel++
        generateMap()
    }

    private val bodyMapper = mapperFor<BodyComponent>()
    private fun generateMap() {

        var randomAngle = (0f..360f)
        val startVector = Vector2.X.cpy().scl(100f).setAngleDeg(randomAngle.random())

        player.touchedObjectives.clear()
        numberOfEnemies = 2f.pow(currentLevel).roundToInt() * 10
        numberOfObjectives = 2f.pow(currentLevel).roundToInt()

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

        for (x in 1..25)
            for (y in 1..25) {
                obstacle(x * randomFactor.random(), y * randomFactor.random())
            }

        val position = transformMapper.get(player.entity).position.cpy()

        for(i in 0 until numberOfObjectives) {
            position.add(startVector)
            objective(position.x, position.y)

            for(e in 0 until numberOfEnemies)
                enemy(position.x + enemyRandomFactor.random(), position.y + enemyRandomFactor.random())

            randomAngle = (0f..(randomAngle.endInclusive / 2f))
            startVector.setAngleDeg(randomAngle.random())
        }



    }


    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.4f, .4f, .4f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        //Update viewport and camera here and nowhere else...

        camera.update(true)
        batch.projectionMatrix = camera.combined
        engine.update(delta)
        ui.update(delta)
        audioPlayer.update(delta)
        if(player.touchedObjectives.count() == numberOfObjectives) //Add check if we killed all enemies
            nextLevel()
    }

    override fun resize(width: Int, height: Int) {
        initializeIfNeeded()
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
    }

    private fun initializeIfNeeded() {
        if (needsInit) {
            Gdx.gl.glClearColor(.4f, .4f, .4f, 1f)
            Assets.load()
            setupInput()
            generateMap()
            camera.setToOrtho(true, viewPort.maxWorldWidth, viewPort.maxWorldHeight)
            needsInit = false
        }
    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
    }

    override fun dispose() {
        // Destroy screen's assets here.
    }
}
