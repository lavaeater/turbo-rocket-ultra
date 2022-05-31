package screens

import audio.AudioPlayer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import injection.Context
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import statemachine.StateMachine

abstract class BasicScreen(val gameState: StateMachine<GameState, GameEvent>) : KtxScreen, KtxInputAdapter {

    open val camera: OrthographicCamera by lazy { Context.inject() }
    open val viewport: ExtendViewport by lazy { Context.inject() }
    protected val batch: PolygonSpriteBatch by lazy { Context.inject() }
    protected val audioPlayer: AudioPlayer by lazy { Context.inject() }

    override fun show() {
        Gdx.input.inputProcessor = this
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.4f, .4f, .4f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update(true)
        batch.projectionMatrix = camera.combined

    }
}