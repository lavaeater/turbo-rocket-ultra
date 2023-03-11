package common.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import eater.core.MainGame

abstract class ScreenWithStage(mainGame: MainGame, clearColor: Color): BasicScreen(mainGame, clearColor) {
    abstract val stage: Stage
    override fun render(delta: Float) {
        super.render(delta)
        stage.act(delta)
        stage.draw()
    }
}