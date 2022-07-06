package ui.new

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import eater.physics.drawScaled

open class BoundTextureElement<T: Any>(
    textureFunc: (T)-> TextureRegion,
    position: Vector2 = vec2(),
    val rotation: Float = 0f,
    val scale: Float = 1f,
    parent: AbstractElement? = null
) : BoundElement<T, TextureRegion>(textureFunc, position, parent = parent) {

    override val bounds: Rectangle
        get() = Rectangle(
            actualPosition.x,
            actualPosition.y,
            valueFunc(currentItem).regionWidth * scale,
            valueFunc(currentItem).regionHeight * scale
        )

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        super.render(batch, delta, scale, debug)
        batch.drawScaled(
            valueFunc(currentItem),
            actualPosition.x,
            actualPosition.y,
            this.scale * scale,
            rotation
        )
    }
}