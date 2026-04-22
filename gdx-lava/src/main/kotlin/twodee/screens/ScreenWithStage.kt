package twodee.screens

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import twodee.core.MainGame

abstract class ScreenWithStage(
    mainGame: MainGame,
    viewport: Viewport,
    camera: OrthographicCamera,
    batch: PolygonSpriteBatch
) : BasicScreen(
    mainGame,
    camera,
    viewport,
    batch
) {
    abstract val stage: Stage
    override fun render(delta: Float) {
        super.render(delta)
        actAndRenderStage(delta)
    }

    open fun actAndRenderStage(delta:Float) {
        stage.act(delta)
        stage.draw()
    }
}
