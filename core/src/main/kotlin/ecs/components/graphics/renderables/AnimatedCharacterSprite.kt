package ecs.components.graphics.renderables

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

class AnimatedCharacterSprite(private val anims: Map<AnimState, LpcCharacterAnim>, val scale: Float = 1f, val offsetX: Float = 0f, val offsetY: Float = 0f) : Renderable {

    var currentAnimState : AnimState = anims.values.first().state
    var currentDirection: SpriteDirection = SpriteDirection.South
    val currentAnim : Animation<TextureRegion> get() = anims[currentAnimState]!!.animations[currentDirection]!!

    fun currentTextureRegion(animationStateTime: Float): TextureRegion {
        return currentAnim.getKeyFrame(animationStateTime)
    }

    override val renderableType: RenderableType
        get() = RenderableType.AnimatedCharacterSprite

    override fun render(
        position: Vector2,
        rotation:Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer) {

        val currentTextureRegion = currentTextureRegion(animationStateTime)

        batch.drawScaled(
            currentTextureRegion(animationStateTime),
            (position.x + (currentTextureRegion.regionWidth / 2 * scale) + (offsetX * scale * this.scale)),
            (position.y + (currentTextureRegion.regionHeight / 2 * scale) + (offsetY * scale * this.scale)),
            scale * this.scale
        )
        //Debug thing
        //shapeDrawer.filledCircle(isoPos, .2f, Color.GREEN)
    }
}

