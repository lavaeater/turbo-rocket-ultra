package ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

open class RepeatingTextureActor(
    open val repeatFor: Int,
    private val offset: Vector2,
    textureRegion: TextureRegion,
    scale: Float
) : TextureActor(
    textureRegion,
    vec2(),
    scale
) {

    private val spaceVector = vec2()
    override fun render(batch: Batch, parentPosition: Vector2, debug: Boolean) {
        for (n in 0 until repeatFor)
            super.render(batch, spaceVector.set(parentPosition.x + n * offset.x, parentPosition.y - n * offset.y), debug)
    }
}