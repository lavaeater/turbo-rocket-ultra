package screens

import audio.AudioPlayer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import injection.Context
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import statemachine.StateMachine

abstract class BasicScreen(val gameState: StateMachine<GameState, GameEvent>) : KtxScreen, KtxInputAdapter, ControllerListener {

    open val camera: OrthographicCamera by lazy { Context.inject() }
    open val viewport: ExtendViewport by lazy { Context.inject() }
    protected val batch: PolygonSpriteBatch by lazy { Context.inject() }
    protected val audioPlayer: AudioPlayer by lazy { Context.inject() }

    override fun show() {
        Gdx.input.inputProcessor = this
        Controllers.addListener(this)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        Controllers.removeListener(this)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.4f, .4f, .4f, 1f)
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

    override fun povMoved(controller: Controller, povCode: Int, value: PovDirection?): Boolean {
        return true
    }

    override fun xSliderMoved(controller: Controller, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun ySliderMoved(controller: Controller, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun accelerometerMoved(controller: Controller, accelerometerCode: Int, value: Vector3?): Boolean {
        return true
    }
}