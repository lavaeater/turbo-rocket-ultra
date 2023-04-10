package screens

import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction

class BoundMoveToAction(val xFunc: () -> Float, val yFunc: () -> Float) : MoveToAction() {
    override fun act(delta: Float): Boolean {
        x = xFunc()
        y = yFunc()
        return super.act(delta)
    }
}