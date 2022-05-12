package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

interface ControlMapper: Component {

    var needsReload: Boolean
    val isKeyboard: Boolean
    val isGamepad: Boolean
    val aimVector: Vector2
    var aiming: Boolean
    val mousePosition: Vector2
    var firing: Boolean
    var turning: Float
    var thrust: Float
    var needToChangeGun: InputIndicator
    var doContextAction: Boolean
    var isInBuildMode: Boolean
    var buildIfPossible: Boolean

    val walkVector: Vector2
    val controllerId: String

    fun setAimVector(screenX: Int, screenY: Int, position: Vector2)
    var uiControl: UserInterfaceControl
    var requireSequencePress: Boolean
    var keyPressedCallback: (Int) -> Unit
}

