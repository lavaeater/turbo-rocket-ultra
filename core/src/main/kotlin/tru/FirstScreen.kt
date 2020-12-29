package tru

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import control.InputManager
import control.ShipControl
import factories.box
import injection.Context
import ktx.math.random
import ui.IUserInterface


class FirstScreen : Screen {

    companion object {
        const val SHIP_DENSITY = 1f
        const val SHIP_LINEAR_DAMPING = 10f
        const val SHIP_ANGULAR_DAMPING = 10f

        const val GAMEWIDTH = 72f
        const val GAMEHEIGHT = 48f
    }

    private var needsInit = true

    private val camera: OrthographicCamera by lazy { Context.inject() }
    private val viewPort: ExtendViewport by lazy { Context.inject() }
    private val control: ShipControl by lazy { Context.inject() }
    private val engine: Engine by lazy { Context.inject() }
    private val batch: PolygonSpriteBatch by lazy { Context.inject() }
    private val ui: IUserInterface by lazy { Context.inject() }

    override fun show() {
        if (needsInit) {
            Assets.load()
            setupInput()
            generateMap()
            camera.setToOrtho(true, viewPort.maxWorldWidth, viewPort.maxWorldHeight)
            needsInit = false
        }
    }

    private fun setupInput() {
        Gdx.input.inputProcessor = InputManager(control)
    }

    private fun generateMap() {
        val randomFactor = 0f..15f

        for (x in 0..99)
            for (y in 0..99) {
                box(x * 25f + randomFactor.random(), y * 25f + randomFactor.random())
            }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.3f, .5f, .8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        //Update viewport and camera here and nowhere else...

        camera.update(true)
        batch.projectionMatrix = camera.combined
        engine.update(delta)
        ui.update(delta)

    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height)
        batch.projectionMatrix = camera.combined
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
