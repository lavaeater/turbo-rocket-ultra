package systems.graphics

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.math.vec2
import systems.graphics.GameConstants.GAME_HEIGHT
import systems.graphics.GameConstants.GAME_WIDTH

class SplitScreenState {
    var isSplit = false

    class PlayerView(val camera: OrthographicCamera = OrthographicCamera()) {
        val viewport = ExtendViewport(GAME_WIDTH, GAME_HEIGHT, camera)
        val targetPosition = vec2()
        val position = vec2()
    }

    val playerViews = mutableListOf<PlayerView>()

    fun ensureViews(count: Int) {
        while (playerViews.size < count) playerViews.add(PlayerView())
        while (playerViews.size > count) playerViews.removeLast()
    }

    fun resize(screenWidth: Int, screenHeight: Int) {
        if (isSplit && playerViews.size > 1) {
            val halfWidth = screenWidth / playerViews.size
            playerViews.forEachIndexed { i, view ->
                view.viewport.update(halfWidth, screenHeight)
                view.camera.update(false)
            }
        }
    }
}
