package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.Player
import data.Players
import data.SelectedItemList
import data.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import input.Button
import input.ControlMapper
import input.GamepadControl
import input.KeyboardControl
import ktx.log.debug
import ktx.scene2d.*
import statemachine.StateMachine
import tru.AnimState
import tru.Assets
import tru.SpriteDirection
import ui.customactors.animatedSpriteImage
import ui.customactors.boundLabel
import kotlin.properties.Delegates

object ApplicationFlags {
    val map = mutableMapOf("showEnemyPaths" to false, "showEnemyActionInfo" to true, "showCanSee" to false, "showMemory" to true)
    var showEnemyPaths by map
    var showCanSee by map
    var showEnemyActionInfo by map
    var showMemory by map
}


class SetupScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    override val camera = OrthographicCamera()
    override val viewport = ExtendViewport(800f, 600f, camera)

    private val defaultKeyMap = command("Default") {
        setUp(Input.Keys.SPACE, "Toggle Player") { toggleKeyboardPlayer() }
        setUp(Input.Keys.LEFT, "Prev Character") { changeSpriteKeyboard(-1) }
        setUp(Input.Keys.RIGHT, "Next Character") { changeSpriteKeyboard(1) }
        setUp(Input.Keys.ENTER, "Start Game") { startGame() }
        setUp(Input.Keys.D, "Debug Mode On") { toggleDebugMode() }
    }

    private val debugModeKeyMap = command("Normal") {
        setUp(Input.Keys.SPACE, "Toggle Player") { toggleKeyboardPlayer() }
        setUp(Input.Keys.LEFT, "Prev Character") { changeSpriteKeyboard(-1) }
        setUp(Input.Keys.RIGHT, "Next Character") { changeSpriteKeyboard(1) }
        setUp(Input.Keys.ENTER, "Start Game") { startGame() }
        setUp(Input.Keys.T, "Start Game with AI") { startGameWithAi() }
        setUp(Input.Keys.C, "Concept Screen") { startConceptScreen() }
        setUp(Input.Keys.A, "Animation Editor") { startAnimEditor() }
        setUp(Input.Keys.UP, "Next map") { nextMap() }
        setUp(Input.Keys.DOWN, "Previous map") { previousMap() }
        setUp(Input.Keys.M, "Map Editor") {
            gameState.acceptEvent(GameEvent.StartMapEditor)
        }
        setUp(Input.Keys.D, "Debug Mode Off") { toggleDebugMode() }
    }

    private var currentKeyMap = defaultKeyMap

    private var debugMode = false
    private fun toggleDebugMode() {
        debugMode = !debugMode
        if (debugMode)
            currentKeyMap = debugModeKeyMap
        else
            currentKeyMap = defaultKeyMap
    }

    private fun startAnimEditor() {
        gameState.acceptEvent(GameEvent.StartAnimEditor)
    }

    val debug = false
    val shapeDrawer by lazy { Assets.shapeDrawer }
    private val setupViewModel = SetupViewModel()
    private val availableControllers get() = setupViewModel.availableControllers

    private val activePlayers = mutableListOf<Pair<ControlMapper, Player>>()

    private val playerCards: HorizontalGroup by lazy {
        scene2d.horizontalGroup {
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
            table {
                boundLabel({ currentKeyMap.toString() })
                boundLabel({mapNames.selectedItem})
                setPosition(450f, 350f)
                pack()
            }
        }
        aStage.addActor(playerCards)
        aStage
    }

    private fun cardForPlayerModel(playerModel: PlayerModel): Actor {
        return scene2d.verticalGroup {
            userObject = playerModel
            isVisible = true
            val notSelectedGroup = verticalGroup {
                columnAlign(Align.left)
                isVisible = true
                verticalGroup {
                    columnAlign(Align.left)
                    horizontalGroup {
                        label("Press ")
                        when (playerModel) {
                            is PlayerModel.Keyboard -> label("[Space]")
                            is PlayerModel.GamePad -> image(Button.Cross.image)
                        }

                    }
                    label(" to join")
                }
            }
            addActor(notSelectedGroup)
            val selectedGroup = verticalGroup {
                columnAlign(Align.left)
                isVisible = false
                boundLabel({ playerModel.name })
                boundLabel({ playerModel.selectedCharacter })
                val ai = animatedSpriteImage(
                    Assets.characterTurboAnims.first().animationFor(AnimState.Walk, SpriteDirection.South)
                ) {}
                addActor(ai)
                verticalGroup {
                    columnAlign(Align.left)
                    horizontalGroup {
                        label("Press ")
                        when (playerModel) {
                            is PlayerModel.Keyboard -> label("[Return]")
                            is PlayerModel.GamePad -> image(Assets.ps4Buttons["square"]!!)
                        }

                    }
                    label(" to start")
                    label("")
                    when (playerModel) {
                        is PlayerModel.Keyboard -> {
                            label("WASD - walk about")
                            label("R - reload")
                            label("Mouse - aim")
                            label("B - build")
                            label("LMB - shoot")
                            label("Wheel - change weapon")
                        }
                        is PlayerModel.GamePad -> {
                            label("Left Stick - move")
                            label("Right Stick - aim")
                            horizontalGroup {
                                image(Button.Square.image)
                                label(" - reload")
                            }
                            horizontalGroup {
                                image(Button.Triangle.image)
                                label(" - build")
                            }
                            horizontalGroup {
                                image(Button.DPadLeft.image)
                                image(Button.DPadRight.image)
                                label(" - change weapon")
                            }
                        }
                    }
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

            playerModel.isSelectedCallback = { isSelected ->
                selectedGroup.isVisible = isSelected
                notSelectedGroup.isVisible = !isSelected
                pack()
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

    override fun keyDown(keycode: Int): Boolean {
        return currentKeyMap.execute(keycode, KeyPress.Down)
    }

    override fun keyUp(keycode: Int): Boolean {
        return currentKeyMap.execute(keycode, KeyPress.Up)
    }

    var mapNames = getMapList()

    fun getMapList(): SelectedItemList<String> {
        debug { Gdx.files.localStoragePath }
        val mapFiles = Gdx.files.local("text_maps").list()
        debug { "Have found ${mapFiles.size} files" }
        return selectedItemListOf(*mapFiles.map { it.file().nameWithoutExtension }.toTypedArray())
    }

    fun checkMapList() {
        val updatedList = getMapList()
        if (updatedList.size > mapNames.size) {
            mapNames = updatedList
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


    private fun startGameWithAi(): Boolean {
        Players.players[KeyboardControl()] = Player("AI PLAYER", true).apply {
            selectedCharacterSpriteName = Assets.characterTurboAnims.first().name
        }
        MapList.mapFileNames.clear()
        for (name in mapNames.withSelectedItemFirst) {
            MapList.mapFileNames.add(name)
        }
        gameState.acceptEvent(GameEvent.StartedGame)
        return true
    }

    private fun startGame(): Boolean {
        /* Take players we have here and add them to the game or something.
        This is just a stop-over for later.
         */
        for (model in availableControllers.filter { it.isSelected }) {
            when (model) {
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
        for (name in mapNames.withSelectedItemFirst) {
            MapList.mapFileNames.add(name)
        }
        gameState.acceptEvent(GameEvent.StartedGame)
        return true
    }

    private fun startCharacterEditor(): Boolean {
        gameState.acceptEvent(GameEvent.StartCharacterEditor)
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
        availableControllers.firstOrNull { it is PlayerModel.Keyboard }?.toggle()
        return true
    }

}
