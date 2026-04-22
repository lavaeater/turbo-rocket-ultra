package screens.basic

import audio.AudioPlayer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dependencies.InjectionContext.Companion.inject
import gamestate.GameEvent
import gamestate.GameState
import de.eskalon.commons.screen.ManagedScreen
import ktx.app.KtxInputAdapter
import statemachine.StateMachine
import animation.Assets

abstract class BasicScreen(val gameState: StateMachine<GameState, GameEvent>) : ManagedScreen(), KtxInputAdapter, ControllerListener {

    open val camera: OrthographicCamera by lazy { inject() }
    open val viewport: Viewport by lazy { inject<ExtendViewport>() }
    protected val batch: PolygonSpriteBatch by lazy { inject() }
    protected val audioPlayer: AudioPlayer by lazy { inject() }

    init {
        addInputProcessor(this)
    }

    override fun show() {
        Controllers.addListener(this)
    }

    override fun hide() {
        Controllers.removeListener(this)
    }

    override fun resize(width: Int, height: Int) {}
    override fun dispose() {}

    private val bgColor by lazy { Assets.backgroundColor }
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update(true)
        batch.projectionMatrix = camera.combined

    }

    override fun connected(controller: Controller) {
    }

    override fun disconnected(controller: Controller) {
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        return true
    }

    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        return true
    }

    override fun axisMoved(controller: Controller, axisCode: Int, value: Float): Boolean {
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false
}