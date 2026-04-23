package systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import components.TransformComponent
import physics.getComponent
import components.graphics.CameraFollowComponent
import ktx.ashley.allOf
import ktx.math.vec2
import ktx.math.vec3

class CameraUpdateSystem(
    private val camera: OrthographicCamera,
    private val viewport: ExtendViewport,
    val splitState: SplitScreenState = SplitScreenState()
) :
    IteratingSystem(
        allOf(
            CameraFollowComponent::class,
            TransformComponent::class
        ).get()) {

    private val transformComponents = mutableListOf<TransformComponent>()
    private val cameraPosition = vec2()

    companion object {
        private const val SPLIT_THRESHOLD = GameConstants.GAME_WIDTH * 3f
        private const val MERGE_THRESHOLD = GameConstants.GAME_WIDTH * 2f
    }

    fun reset() {
        transformComponents.clear()
        cameraPosition.set(Vector2.Zero)
        splitState.isSplit = false
        splitState.playerViews.clear()
    }

    override fun update(deltaTime: Float) {
        transformComponents.clear()
        super.update(deltaTime)
        if (transformComponents.isEmpty()) return

        val minX = transformComponents.minOf { it.position.x }
        val maxX = transformComponents.maxOf { it.position.x }
        val minY = transformComponents.minOf { it.position.y }
        val maxY = transformComponents.maxOf { it.position.y }
        val spreadX = maxX - minX
        val spreadY = maxY - minY
        val spread = maxOf(spreadX, spreadY)

        if (!splitState.isSplit && spread > SPLIT_THRESHOLD && transformComponents.size > 1) {
            splitState.isSplit = true
            splitState.ensureViews(transformComponents.size)
            transformComponents.forEachIndexed { i, tc ->
                splitState.playerViews[i].position.set(tc.position)
                splitState.playerViews[i].targetPosition.set(tc.position)
            }
        } else if (splitState.isSplit && spread < MERGE_THRESHOLD) {
            splitState.isSplit = false
        }

        if (splitState.isSplit) {
            updateSplitCameras(deltaTime)
        } else {
            updateSingleCamera(minX, maxX, minY, maxY)
        }
    }

    private fun updateSingleCamera(minX: Float, maxX: Float, minY: Float, maxY: Float) {
        cameraPosition.set(
            transformComponents.map { it.position.x }.sum() / transformComponents.size,
            transformComponents.map { it.position.y }.sum() / transformComponents.size
        )
        camera.position.lerp(vec3(cameraPosition, 0f), 0.5f)

        viewport.minWorldWidth = ((maxX - minX) + 30f).coerceIn(GameConstants.GAME_WIDTH, GameConstants.GAME_WIDTH * 5)
        viewport.minWorldHeight = ((maxY - minY) + 30f).coerceIn(GameConstants.GAME_HEIGHT, GameConstants.GAME_HEIGHT * 5)
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        camera.update()
    }

    private fun updateSplitCameras(deltaTime: Float) {
        splitState.ensureViews(transformComponents.size)
        transformComponents.forEachIndexed { i, tc ->
            val view = splitState.playerViews[i]
            view.targetPosition.set(tc.position)
            view.position.lerp(view.targetPosition, 0.1f)
            view.camera.position.set(view.position, 0f)
            view.camera.viewportWidth = GameConstants.GAME_WIDTH
            view.camera.viewportHeight = GameConstants.GAME_HEIGHT
            view.camera.update(false)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        transformComponents.add(entity.getComponent())
    }
}
