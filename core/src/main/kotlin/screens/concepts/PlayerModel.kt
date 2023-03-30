package screens.concepts

import com.badlogic.gdx.controllers.Controller
import data.SelectedItemList
import ktx.log.debug
import tru.Assets
import tru.TurboCharacterAnim

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

    class Keyboard : PlayerModel("Keyboard", Assets.characterTurboAnims.first().name) {
        init {
            debug { "Keyboard" }
        }
    }
    class GamePad(val controller: Controller) :
        PlayerModel("GamePad ${controller.playerIndex + 1}", Assets.characterTurboAnims.first().name) {
        init {
            debug { "Added for ${controller.uniqueId}" }
        }
    }

}