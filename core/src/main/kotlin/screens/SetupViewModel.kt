package screens

import com.badlogic.gdx.controllers.Controllers
import screens.concepts.PlayerModel

class SetupViewModel {
    val availableControllers: MutableList<PlayerModel> by lazy {
        val controllers = Controllers.getControllers()
        mutableListOf(
            *Controllers.getControllers().map { PlayerModel.GamePad(it) }.toTypedArray(),
            PlayerModel.Keyboard()
        )
    }
}