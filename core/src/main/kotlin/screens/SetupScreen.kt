package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.Player
import data.Players
import ecs.systems.player.SelectedItemList
import ecs.systems.player.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import input.Button
import input.ControlMapper
import input.GamepadControl
import input.KeyboardControl
import ktx.scene2d.*
import statemachine.StateMachine
import tru.*
import ui.customactors.animatedSpriteImage
import ui.customactors.boundLabel

class SetupViewModel {
    val availableControllers: MutableList<PlayerModel> = mutableListOf(
        *Controllers.getControllers().map { PlayerModel.GamePad(it) }.toTypedArray(),
        PlayerModel.Keyboard()
    )
}

open class BoundHorizontalGroup : HorizontalGroup() {
}

sealed class PlayerModel(
    val name: String,
    var selectedCharacter: String
) {
    lateinit var isSelectedCallback: (Boolean) -> Unit
    lateinit var selectedAbleSpriteAnims: SelectedItemList<TurboCharacterAnim>
    var isSelected = false
    fun toggle() {
        isSelected = !isSelected
        isSelectedCallback(isSelected)
    }

    class Keyboard : PlayerModel("Keyboard", Assets.characterTurboAnims.first().name)
    class GamePad(val controller: Controller) :
        PlayerModel("GamePad ${controller.playerIndex + 1}", Assets.characterTurboAnims.first().name)
}

class SetupScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(800f, 600f, camera)

    val debug = false
    val shapeDrawer by lazy { Assets.shapeDrawer }
    private val setupViewModel = SetupViewModel()
    private val availableControllers get() = setupViewModel.availableControllers

    private val activePlayers = mutableListOf<Pair<ControlMapper, Player>>()

    private val playerCards: HorizontalGroup by lazy {
        scene2d.horizontalGroup {
//            setFillParent(true)
            setPosition(25f, 200f)
            for (model in setupViewModel.availableControllers) {
                addActor(cardForPlayerModel(model))
            }
        }
    }

    private val stage by lazy {
        val aStage = Stage(viewport, batch)
        aStage.isDebugAll = false
        aStage.actors {
            boundLabel({mapNames.selectedItem}) {

        } }
        aStage.addActor(playerCards)
        aStage
    }

    private fun cardForPlayerModel(playerModel: PlayerModel): Actor {
        return scene2d.verticalGroup {
            userObject = playerModel
            isVisible = true
            val selectedGroup = verticalGroup {
                isVisible = false
                boundLabel({ playerModel.name })
                boundLabel({ playerModel.selectedCharacter })
                val ai = animatedSpriteImage(
                    Assets.characterTurboAnims.first().animationFor(AnimState.Walk, SpriteDirection.South)
                ) {}
                addActor(ai)
                verticalGroup {
                    horizontalGroup {
                        label("Press ")
                        when (playerModel) {
                            is PlayerModel.Keyboard -> label("[Return]")
                            is PlayerModel.GamePad -> image(Assets.ps4Buttons["square"]!!)
                        }

                    }
                    label(" to start")
                }
                playerModel.selectedAbleSpriteAnims = selectedItemListOf(
                    { anim ->
                        playerModel.selectedCharacter = anim.name
                        ai.animation = anim.animationFor(AnimState.Walk, SpriteDirection.South)
                    },
                    *Assets.characterTurboAnims.toTypedArray()
                )
            }
            addActor(selectedGroup)
            val notSelectedGroup = verticalGroup {
                isVisible = true
                verticalGroup {
                    horizontalGroup {
                        label("Press ")
                        when (playerModel) {
                            is PlayerModel.Keyboard -> label("[Space]")
                            is PlayerModel.GamePad -> image(Assets.ps4Buttons["cross"]!!)
                        }

                    }
                    label(" to join")
                }
            }
            addActor(notSelectedGroup)
            playerModel.isSelectedCallback = { isSelected ->
                selectedGroup.isVisible = isSelected
                notSelectedGroup.isVisible = !isSelected
            }

            color = Color.RED
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        stage.act(delta)
        stage.draw()
    }


    override fun connected(controller: Controller) {
        super.connected(controller)
        val gp = availableControllers.firstOrNull { it is PlayerModel.GamePad && it.controller == controller }
        if (gp == null) {
            val newModel = PlayerModel.GamePad(controller)
            availableControllers.add(newModel)
            playerCards.addActor(cardForPlayerModel(newModel))
        }
    }

    override fun disconnected(controller: Controller) {
        super.disconnected(controller)
        val gp = availableControllers.firstOrNull { it is PlayerModel.GamePad && it.controller == controller }
        if (gp != null) {
            val card = playerCards.children.firstOrNull { it.userObject == gp }
            card?.remove()
            availableControllers.remove(gp)
        }
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false)
        viewport.update(width, height, true)
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    override fun keyUp(keycode: Int): Boolean {
        return when (keycode) {
            Input.Keys.SPACE -> toggleKeyboardPlayer()
            Input.Keys.LEFT -> changeSpriteKeyboard(-1)
            Input.Keys.RIGHT -> changeSpriteKeyboard(1)
            Input.Keys.ENTER -> startGame()
            Input.Keys.C -> startConceptScreen()
            Input.Keys.E -> startEditor()
            Input.Keys.A -> previousMap()
            Input.Keys.S -> nextMap()
            Input.Keys.M -> {
                gameState.acceptEvent(GameEvent.StartMapEditor)
                true
            }
            else -> super.keyUp(keycode)
        }
    }

    var mapNames = selectedItemListOf("default")
    fun checkMapList() {
        val mapFiles = Gdx.files.local("text_maps").list()
        if(mapFiles.size > mapNames.size) {
            mapNames = selectedItemListOf("default", *mapFiles.map { it.file().nameWithoutExtension }.toTypedArray())
        }
    }

    private fun nextMap(): Boolean {
        checkMapList()
        mapNames.nextItem()
        return true
    }

    private fun previousMap(): Boolean {
        checkMapList()
        mapNames.previousItem()
        return true
    }

    private fun startConceptScreen(): Boolean {
        gameState.acceptEvent(GameEvent.StartConcept)
        return true
    }

    private fun changeSpriteKeyboard(indexChange: Int): Boolean {
        availableControllers.first { it is PlayerModel.Keyboard }.apply {
            if (indexChange < 0) this.selectedAbleSpriteAnims.previousItem() else this.selectedAbleSpriteAnims.nextItem()
        }
        return true
    }

    private fun startGame(): Boolean {
        /* Take players we have here and add them to the game or something.
        This is just a stop-over for later.
         */
        for(model in availableControllers.filter { it.isSelected }) {
            when(model) {
                is PlayerModel.Keyboard -> Players.players[KeyboardControl()] = Player(model.name).apply {
                    selectedCharacterSpriteName = model.selectedAbleSpriteAnims.selectedItem.key
                }
                is PlayerModel.GamePad -> Players.players[GamepadControl(model.controller)] = Player(model.name).apply {
                    selectedCharacterSpriteName = model.selectedAbleSpriteAnims.selectedItem.key
                }
            }

        }
        //

        MapList.mapFileNames.clear()
        for(name in mapNames.withSelectedItemFirst) {
            MapList.mapFileNames.add(name)
        }
        gameState.acceptEvent(GameEvent.StartedGame)
        return true
    }

    private fun startEditor(): Boolean {
        gameState.acceptEvent(GameEvent.StartEditor)
        return true
    }


    override fun buttonUp(controller: Controller, buttonCode: Int): Boolean {
        return when (Button.getButton(buttonCode)) {
            Button.Cross -> toggleController(controller)
            Button.Square -> startGame()
            Button.DPadLeft -> changeSprite(controller, -1)
            Button.DPadRight -> changeSprite(controller, 1)
            else -> super.buttonUp(controller, buttonCode)
        }
    }

    private fun toggleController(controller: Controller): Boolean {
        availableControllers.firstOrNull { it is PlayerModel.GamePad && it.controller == controller }?.toggle()
        return true
    }

    private fun changeSprite(controller: Controller, indexChange: Int): Boolean {
        availableControllers.first { it is PlayerModel.GamePad && it.controller == controller }.apply {
            if (indexChange < 0) selectedAbleSpriteAnims.previousItem() else selectedAbleSpriteAnims.nextItem()
        }
        return true
    }

    private fun toggleKeyboardPlayer(): Boolean {
        availableControllers.firstOrNull() { it is PlayerModel.Keyboard }?.toggle()
        return true
    }

}
