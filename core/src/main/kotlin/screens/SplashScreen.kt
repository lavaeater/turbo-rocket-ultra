package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.scenes.scene2d.ui.Table
import gamestate.GameEvent
import gamestate.GameState
import gamestate.Player
import gamestate.Players
import input.Button
import input.GamepadControl
import input.KeyboardControl
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.scene2d.table//        setScreen<GameScreen>()
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
                pad(10f)
            }

            stage.addActor(rootTable)
            needInit = false
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        return when(keycode) {
            Input.Keys.SPACE -> toggleKeyboardPlayer()
            Input.Keys.ENTER -> startGame()
            Input.Keys.S -> go2Setup()
            else -> super.keyUp(keycode)
        }
    }

    private fun go2Setup(): Boolean {
        gameState.acceptEvent(GameEvent.LeftSplash)
        return true
    }

    private fun startGame(): Boolean {
        gameState.acceptEvent(GameEvent.StartedGame)
        return true
    }

    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        return when(Button.getButton(buttonCode)) {
            Button.Green -> addIfNotAdded(controller)
            Button.Blue -> startGame()
            Button.Red -> removeIfAdded(controller)
            else -> super.buttonUp(controller, buttonCode)
        }
    }

    private fun removeIfAdded(controller: Controller): Boolean {
        val toRemove = Players.players.filterKeys { it.isGamepad && (it as GamepadControl).controller == controller}.keys.firstOrNull()

        if(toRemove != null) {
            Players.players.remove(toRemove)
            return true
        }
        return false
    }

    private fun addIfNotAdded(controller: Controller): Boolean {
        if(!Players.players.filterKeys { it.isGamepad && (it as GamepadControl).controller == controller }.any() ) {
            Players.players[GamepadControl(controller)] = Player()
            return true
        }
        return false
    }

    private fun toggleKeyboardPlayer(): Boolean {
        val toRemove = Players.players.filter { it.key.isKeyboard }.keys.firstOrNull()
        if(toRemove == null) {
            Players.players[KeyboardControl()] = Player()
            return true
        } else {
            Players.players.remove(toRemove)
            return false
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false)
        viewport.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }
}

