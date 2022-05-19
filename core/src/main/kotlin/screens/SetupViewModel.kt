package screens

import com.badlogic.gdx.controllers.Controllers

class SetupViewModel {
    val availableControllers: MutableList<PlayerModel> by lazy {
        val controllers = Controllers.getControllers()
        mutableListOf(
            *Controllers.getControllers().map { PlayerModel.GamePad(it) }.toTypedArray(),
            PlayerModel.Keyboard()
        )
    }
}