package screens

import ktx.actors.KtxInputListener
import ktx.actors.stage

abstract class UserInterfaceScreen : BasicScreen() {
    protected val stage = stage(batch, viewPort)
    override fun render(delta: Float) {
        super.render(delta)
        stage.act(delta)
        stage.draw()
    }
}