package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import ecs.components.player.PlayerMode

interface ControlMapper: Component {

    val isKeyboard: Boolean
    val isGamepad: Boolean
    val aimVector: Vector2
    var aiming: Boolean
    val mousePosition: Vector2
    var firing: Boolean
    var turning: Float
    var thrust: Float

    val walkVector: Vector2
    val controllerId: String
    var playerMode: PlayerMode

    fun setAimVector(screenX: Int, screenY: Int, position: Vector2)
    var uiControl: UserInterfaceControl
}

interface UserInterfaceControl {
    fun left()
    fun right()
    fun cancel()
    fun select()
}

open class NoOpUserInterfaceControl : UserInterfaceControl {

    companion object {
        val control = NoOpUserInterfaceControl()
    }

    override fun left() {

    }

    override fun right() {

    }

    override fun cancel() {

    }

    override fun select() {
    }
}

