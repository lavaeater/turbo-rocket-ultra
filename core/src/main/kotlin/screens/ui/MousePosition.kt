package screens.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import eater.injection.InjectionContext.Companion.inject
import ecs.systems.graphics.GameConstants
import ktx.math.vec2
import ktx.math.vec3

object MousePosition {
    private val mousePosition3D = vec3()
    private val mousePosition2D = vec2()
    private val camera by lazy { inject<OrthographicCamera>() }
    fun toWorld(screenX: Int, screenY: Int): Vector2 {
        mousePosition3D.set(screenX.toFloat(), screenY.toFloat(), 0f)
        camera.unproject(mousePosition3D)
        mousePosition2D.set(mousePosition3D.x, mousePosition3D.y)
        return mousePosition2D
    }

    fun toWorld(): Vector2 {
        return toWorld(Gdx.input.x, Gdx.input.y)
    }

    const val margin = 2.5f
    const val minX = 0f + margin
    const val maxX = GameConstants.GAME_HEIGHT - margin
    const val maxY = GameConstants.GAME_HEIGHT - margin
    const val minY = 0f + margin
}