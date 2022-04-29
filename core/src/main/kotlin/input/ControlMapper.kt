package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

interface ControlMapper: Component {

    val isKeyboard: Boolean
    val isGamepad: Boolean
    val aimVector: Vector2
    val mousePosition: Vector2
    var firing: Boolean
    var turning: Float
    var thrust: Float

    val walkVector: Vector2
    val controllerId: String

    fun setAimVector(screenX: Int, screenY: Int, position: Vector2)



}

