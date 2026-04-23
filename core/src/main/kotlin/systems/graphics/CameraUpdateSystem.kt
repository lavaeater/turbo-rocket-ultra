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

    private val trackedPlayers = mutableListOf<Pair<Entity, TransformComponent>>()
    private val cameraPosition = vec2()

    companion object {
        private const val SPLIT_THRESHOLD = GameConstants.GAME_WIDTH * 1.0f
        private const val MERGE_THRESHOLD = GameConstants.GAME_WIDTH * 0.75f
    }

    fun reset() {
        trackedPlayers.clear()
        cameraPosition.set(Vector2.Zero)
        splitState.isSplit = false
        splitState.playerViews.clear()
    }

    override fun update(deltaTime: Float) {
        trackedPlayers.clear()
        super.update(deltaTime)
        if (trackedPlayers.isEmpty()) return

        val positions = trackedPlayers.map { it.second.position }
        val minX = positions.minOf { it.x }
        val maxX = positions.maxOf { it.x }
        val minY = positions.minOf { it.y }
        val maxY = positions.maxOf { it.y }
        val spread = maxOf(maxX - minX, maxY - minY)

        if (!splitState.isSplit && spread > SPLIT_THRESHOLD && trackedPlayers.size > 1) {
            splitState.isSplit = true
            splitState.ensureViews(trackedPlayers.size)
            trackedPlayers.forEachIndexed { i, (entity, tc) ->
                splitState.playerViews[i].entity = entity
                splitState.playerViews[i].position.set(tc.position)
                splitState.playerViews[i].targetPosition.set(tc.position)
            }
        } else if (splitState.isSplit && spread < MERGE_THRESHOLD) {
            splitState.isSplit = false
        }

        if (splitState.isSplit) {
            updateSplitCameras()
        } else {
            updateSingleCamera(minX, maxX, minY, maxY)
        }
    }

    private fun updateSingleCamera(minX: Float, maxX: Float, minY: Float, maxY: Float) {
        val positions = trackedPlayers.map { it.second.position }
        cameraPosition.set(positions.map { it.x }.sum() / trackedPlayers.size, positions.map { it.y }.sum() / trackedPlayers.size)
        camera.position.lerp(vec3(cameraPosition, 0f), 0.5f)

        viewport.minWorldWidth = ((maxX - minX) + 30f).coerceIn(GameConstants.GAME_WIDTH, GameConstants.GAME_WIDTH * 5)
        viewport.minWorldHeight = ((maxY - minY) + 30f).coerceIn(GameConstants.GAME_HEIGHT, GameConstants.GAME_HEIGHT * 5)
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        camera.update()
    }

    private fun updateSplitCameras() {
        splitState.ensureViews(trackedPlayers.size)
        val slicePixelWidth = Gdx.graphics.width.toFloat() / trackedPlayers.size
        val slicePixelHeight = Gdx.graphics.height.toFloat()
        val worldHeight = GameConstants.GAME_WIDTH * (slicePixelHeight / slicePixelWidth)
        trackedPlayers.forEachIndexed { i, (entity, tc) ->
            val view = splitState.playerViews[i]
            view.entity = entity
            view.targetPosition.set(tc.position)
            view.position.lerp(view.targetPosition, 0.1f)
            view.camera.setToOrtho(true, GameConstants.GAME_WIDTH, worldHeight)
            view.camera.position.set(view.position, 0f)
            view.camera.update(false)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        trackedPlayers.add(entity to entity.getComponent())
    }
}
