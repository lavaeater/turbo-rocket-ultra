package common.ui

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport

abstract class LavaHud(
    val batch: PolygonSpriteBatch,
    hudWidth: Float = 180f,
    aspectRatio: Float = 16f / 9f) {
    private val hudHeight = hudWidth * aspectRatio
    val camera = OrthographicCamera()
    private val hudViewPort = ExtendViewport(hudWidth, hudHeight, camera)
    abstract val stage: Stage
    open fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    open fun resize(width: Int, height: Int) {
        hudViewPort.update(width, height)
    }
}