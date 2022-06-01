package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.ui.Table
import gamestate.GameEvent
import gamestate.GameState
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.scene2d.table
import statemachine.StateMachine
import tru.Assets

class SplashScreen(gameState: StateMachine<GameState, GameEvent>) : UserInterfaceScreen(gameState) {
    override fun show() {
        initSplash()
        super.show()
    }

    lateinit var rootTable: Table
    var needInit = true

    private fun initSplash() {
        if(needInit) {
            rootTable = scene2d.table {
                setFillParent(true)
                image(Assets.splashTexture)
            }
            stage.addActor(rootTable)
            needInit = false
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        return when(keycode) {
            Input.Keys.SPACE -> go2Setup()
            else -> super.keyDown(keycode)
        }
    }

    private fun go2Setup(): Boolean {
        gameState.acceptEvent(GameEvent.LeftSplash)
        return true
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false)
        viewport.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }
}

