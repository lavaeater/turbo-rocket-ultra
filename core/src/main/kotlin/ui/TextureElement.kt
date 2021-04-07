package ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import physics.drawScaled

open class TextureElement(open val texture: TextureRegion, position: Vector2 = vec2(), var rotation: Float = 0f, val scale: Float = 1f, parent: AbstractElement? = null): AbstractElement(position, parent = parent) {
    override val bounds: Rectangle
        get() = Rectangle(actualPosition.x, actualPosition.y, texture.regionWidth * scale, texture.regionHeight * scale)

    override fun render(batch: Batch, delta: Float, debug: Boolean) {
        super.render(batch, delta, debug)
        batch.drawScaled(
            texture,
            actualPosition.x,
            actualPosition.y,
            scale,
            rotation
        )
    }
}