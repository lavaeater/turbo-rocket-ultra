package ui.simple

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import eater.physics.drawScaled

open class TextureActor(
    private val textureRegion: TextureRegion,
    position: Vector2,
    private val scale: Float = 1f
) : LeafActor(position) {
    override fun render(batch: Batch, parentPosition: Vector2, debug: Boolean) {

        batch.drawScaled(
            textureRegion,
            parentPosition.x + position.x - textureRegion.regionWidth * scale / 2,
            parentPosition.y - position.y - textureRegion.regionHeight * scale / 2,
            scale,
            0f
        )
    }
}