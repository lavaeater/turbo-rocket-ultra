package input

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class NumpadControl : ControlMapper, Component {
    override var needsReload = false
    override val isKeyboard = true
    override val isGamepad = false
    override val aimVector = vec2(1f, 0f)
    override var aiming: Boolean = false
    override val mousePosition = vec2()
    override var firing = false
    override var turning = 0f
    override var thrust = 0f
    override var needToChangeGun: InputIndicator = InputIndicator.Neutral
    override var doContextAction: Boolean = false
    override val walkVector: Vector2 = vec2()
        get() = field.set(turning, -thrust)
    override val controllerId: String = "Numpad"
    override var isInBuildMode: Boolean = false
    override var buildIfPossible: Boolean = false

    fun rotateAim(degrees: Float) {
        val angle = MathUtils.atan2(aimVector.y, aimVector.x) * MathUtils.radDeg + degrees
        aimVector.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle))
    }

    override fun setAimVector(screenX: Int, screenY: Int, position: Vector2) {}

    override var uiControl: UserInterfaceControl = NoOpUserInterfaceControl.control
    override var requireSequencePress: Boolean = false
    override var keyPressedCallback: (Int) -> Unit = {}
}
