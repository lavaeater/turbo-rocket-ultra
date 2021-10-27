package ecs.components.graphics.renderables

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer

class RenderableTextureRegion(val textureRegion: TextureRegion, val scale: Float = 1f, val offsetX: Float = 0f, val offsetY: Float = 0f) : Renderable {
    override val renderableType: RenderableType
        get() = RenderableType.Texture
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
            position.x + (this.textureRegion.regionWidth / 2 * scale * this.scale) + (offsetX * scale * this.scale),
            position.y + (this.textureRegion.regionHeight / 2 * scale * this.scale) + (offsetY * scale * this.scale),
            scale * this.scale
        )
        //Debug thing
        shapeDrawer.setColor(color)
       // shapeDrawer.rectangle(position.x, position.y, width, height)
       // shapeDrawer.filledCircle(position, .2f, Color.BLACK)
    }
}