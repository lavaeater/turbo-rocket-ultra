package ecs.components.graphics.renderables

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer

class RenderableTextureRegion(val textureRegion: TextureRegion) : Renderable {
    override val renderableType: RenderableType
        get() = RenderableType.Texture
    val isoPos = vec2()
    override fun render(
        position: Vector2,
        rotation: Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer
    ) {
        val width = 2f
        val height = 2f
        val color = Color.GREEN


        batch.drawScaled(
            this.textureRegion,
            (isoPos.x + (this.textureRegion.regionWidth / 2 * scale)),
            (isoPos.y + (this.textureRegion.regionHeight / 2 * scale)),
            scale
        )
        //Debug thing
        shapeDrawer.setColor(color)
        shapeDrawer.filledCircle(isoPos, .2f, Color.BLACK)
    }
}