package eater.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import eater.core.MainGame
import eater.injection.InjectionContext.Companion.inject
import eater.input.CommandMap
import eater.input.KeyPress
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.assets.disposeSafely

abstract class BasicScreen(val mainGame: MainGame, private val clearColor: Color = Color(0f, 0.2f, .4f, 1f)) : KtxScreen, KtxInputAdapter {

    protected lateinit var commandMap: CommandMap
    open val camera: OrthographicCamera by lazy { inject() }
    open val viewport: Viewport by lazy { inject<ExtendViewport>() }
    open val batch: PolygonSpriteBatch by lazy { inject() }

    override fun show() {
        Gdx.input.inputProcessor = this
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update(true)
        batch.projectionMatrix = camera.combined
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
}