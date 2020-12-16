package control

import com.badlogic.gdx.Input
import ktx.app.KtxInputAdapter

class InputManager(private val shipControl: ShipControl) : KtxInputAdapter {

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> shipControl.throttle(1f)
            Input.Keys.A -> shipControl.turn(-1f)
            Input.Keys.D -> shipControl.turn(1f)
            Input.Keys.SPACE -> shipControl.fire(true)
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> shipControl.throttle(0f)
            Input.Keys.A -> shipControl.turn(0f)
            Input.Keys.D -> shipControl.turn(0f)
            Input.Keys.SPACE -> shipControl.fire(false)
        }
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }

}