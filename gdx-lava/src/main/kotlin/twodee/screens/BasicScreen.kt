package twodee.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import twodee.core.MainGame
import twodee.injection.InjectionContext.Companion.inject
import twodee.input.CommandMap
import twodee.input.KeyPress
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use

abstract class BasicScreen(
    val mainGame: MainGame,
    protected val camera: OrthographicCamera,
    protected val viewport: Viewport,
    protected val batch: PolygonSpriteBatch) : KtxScreen, KtxInputAdapter {

    protected lateinit var commandMap: CommandMap

    override fun show() {
        Gdx.input.inputProcessor = this
        viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun keyDown(keycode: Int): Boolean {
        return commandMap.execute(keycode, KeyPress.Down)
    }

    override fun keyUp(keycode: Int): Boolean {
        return commandMap.execute(keycode, KeyPress.Up)
    }

    override fun dispose() {
        batch.disposeSafely()
    }

    override fun render(delta: Float) {
        clearScreenUpdateCamera(delta)
        batch.use {
            renderBatch(delta)
        }
    }

    abstract fun renderBatch(delta: Float)

    open fun clearScreenUpdateCamera(delta:Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update(true)
        batch.projectionMatrix = camera.combined
    }
}
