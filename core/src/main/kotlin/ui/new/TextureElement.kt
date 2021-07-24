package ui.new

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.collections.toGdxArray
import ktx.math.vec2
import physics.drawScaled

open class TextureElement(
    open val texture: TextureRegion,
    position: Vector2 = vec2(),
    var rotation: Float = 0f,
    val scale: Float = 1f,
    parent: AbstractElement? = null) : AbstractElement(position, parent = parent) {
    override val bounds: Rectangle
        get() = Rectangle(actualPosition.x, actualPosition.y, texture.regionWidth * scale, texture.regionHeight * scale)

    var regionWidth = 32
    var regionHeight = 32
    val frameCount get() =  4//texture.regionWidth / regionWidth
    val animCount get() = texture.regionHeight / regionHeight
    val anims = mutableListOf<Animation<TextureRegion>>()

    fun gridUpdated(gridWidth: Int, gridHeight: Int) {
        anims.clear()
        regionWidth = gridWidth
        regionHeight = gridHeight
        for (row in 0..animCount) {
            var regions = Array<TextureRegion>(frameCount) {
                TextureRegion(texture.texture, it * regionWidth, row * regionHeight, regionWidth, regionHeight)
            }.toGdxArray()

            anims.add(Animation(0.2f, regions, Animation.PlayMode.LOOP))

            regions = Array<TextureRegion>(frameCount - 1) {
                TextureRegion(texture.texture, (it + 4) * regionWidth, row * regionHeight, regionWidth, regionHeight)
            }.toGdxArray()

            anims.add(Animation(0.2f, regions, Animation.PlayMode.LOOP))
        }
    }

    var stateTime = 0f

    override fun render(batch: Batch, delta: Float, scale: Float, debug: Boolean) {
        super.render(batch, delta, scale, debug)
        stateTime += delta
        batch.drawScaled(
            texture,
            actualPosition.x,
            actualPosition.y,
            scale,
            rotation
        )

//        for((index, anim) in anims.withIndex()) {
//            val frame = anim.getKeyFrame(stateTime)
//            var row = if (index < 10) index else index - 10
//            var column = if (index < 10) 0 else 1
//            batch.drawScaled(
//                frame,
//                400f + regionWidth * column,
//                470f - regionHeight * row,
//                scale,
//                rotation
//            )
//        }
    }
}