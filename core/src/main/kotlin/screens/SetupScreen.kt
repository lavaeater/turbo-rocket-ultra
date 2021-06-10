package screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import gamestate.GameEvent
import gamestate.GameState
import gamestate.Player
import gamestate.Players
import input.Button
import input.ControlMapper
import input.GamepadControl
import input.KeyboardControl
import ktx.graphics.use
import ktx.math.vec2
import statemachine.StateMachine
import tru.AnimState
import tru.Assets
import tru.SpriteDirection
import ui.BoundAnimationElement
import ui.BoundTextElement
import ui.CollectionContainerElement

class SetupScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    /*
    Setup screen should be the classic "press x / space to join
    When doing so, you get to see the actual character you are playing and

    SHOULD be able to customize it.

    That would be fudging fabulous.

    Customizing would require that every character was like 300 sprites or something, but it could totally be worth
    it.
     */
    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(800f, 600f, camera)

    val debug = false
    val shapeDrawer by lazy { Assets.shapeDrawer }

    private val activePlayers = mutableListOf<Pair<ControlMapper, Player>>()

    private val altUi = CollectionContainerElement(
        activePlayers,
        listOf(
            BoundTextElement({ p -> p.second.selectedCharacterSpriteName }),
            BoundTextElement({ p -> p.first.controllerId }),
            BoundTextElement({ p -> p.second.kills.toString() }),
            BoundTextElement({ p -> p.second.score.toString() }),
            BoundAnimationElement( { p -> p.second.selectedSprite[AnimState.Walk]!!.animations[SpriteDirection.South]!! })
        ), position = vec2(50f, 400f)
    )

    override fun render(delta: Float) {
        super.render(delta)
        batch.use {
            altUi.render(batch, delta, debug)
        }

    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false)
        viewport.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    override fun keyUp(keycode: Int): Boolean {
        return when(keycode) {
            Input.Keys.SPACE -> toggleKeyboardPlayer()
            Input.Keys.LEFT -> changeSpriteKeyboard(-1)
            Input.Keys.RIGHT -> changeSpriteKeyboard(1)
            Input.Keys.ENTER -> startGame()
            else -> super.keyUp(keycode)
        }
    }

    private fun changeSpriteKeyboard(indexChange: Int): Boolean {
        val playerPair = Players.players.filter { it.key.isKeyboard }.values.firstOrNull()
        if(playerPair != null)
            updateSprite(playerPair, indexChange)
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
            Button.DPadLeft -> changeSprite(controller, -1)
            Button.DPadRight -> changeSprite(controller, 1)
            else -> super.buttonUp(controller, buttonCode)
        }
    }

    private fun updateSprite(player: Player, indexChange: Int) {
        var currentIndex = Assets.playerCharacters.keys.indexOf(player.selectedCharacterSpriteName)
        currentIndex += indexChange
        if(currentIndex < 0)
            currentIndex = Assets.playerCharacters.keys.count() - 1
        else if(currentIndex > Assets.playerCharacters.keys.count() - 1)
            currentIndex = 0

        player.selectedCharacterSpriteName = Assets.playerCharacters.keys.toList()[currentIndex]
    }

    private fun changeSprite(controller: Controller, indexChange: Int): Boolean {
        val playerPair = Players.players.filterKeys { it.isGamepad && (it as GamepadControl).controller == controller}.values.firstOrNull()
        if(playerPair != null) {
            updateSprite(playerPair, indexChange)
        }
        return true
    }

    private fun removeIfAdded(controller: Controller): Boolean {
        val toRemove = Players.players.filterKeys { it.isGamepad && (it as GamepadControl).controller == controller}.keys.firstOrNull()

        if(toRemove != null) {
            Players.players.remove(toRemove)
            updatePlayers()
            return true
        }
        return false
    }

    private fun updatePlayers() {
        activePlayers.clear()
        activePlayers.addAll(Players.players.toList())
    }

    private fun addIfNotAdded(controller: Controller): Boolean {
        if(!Players.players.filterKeys { it.isGamepad && (it as GamepadControl).controller == controller }.any() ) {
            Players.players[GamepadControl(controller)] = Player()
            updatePlayers()
            return true
        }
        return false
    }

    private fun toggleKeyboardPlayer(): Boolean {
        val toRemove = Players.players.filter { it.key.isKeyboard }.keys.firstOrNull()
        return if(toRemove == null) {
            Players.players[KeyboardControl()] = Player()
            updatePlayers()
            true
        } else {
            Players.players.remove(toRemove)
            updatePlayers()
            false
        }
    }

}
