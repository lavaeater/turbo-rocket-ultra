package ecs.components.graphics.renderables

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import isometric.toIsometric
import ktx.math.vec2
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

class AnimatedCharacterSprite(private val anims: Map<AnimState, LpcCharacterAnim>) : Renderable {

    var currentAnimState : AnimState = anims.values.first().state
    var currentDirection: SpriteDirection = SpriteDirection.South
    val currentAnim : Animation<TextureRegion> get() = anims[currentAnimState]!!.animations[currentDirection]!!

    fun currentTextureRegion(animationStateTime: Float): TextureRegion {
        return currentAnim.getKeyFrame(animationStateTime)
    }

    override val renderableType: RenderableType
        get() = RenderableType.AnimatedCharacterSprite

    val isoPos = vec2()

    override fun render(
        position: Vector2,
        rotation:Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer) {

        position.toIsometric(isoPos)

        val currentTextureRegion = currentTextureRegion(animationStateTime)

        batch.drawScaled(
            currentTextureRegion(animationStateTime),
            (isoPos.x + (currentTextureRegion.regionWidth / 2 * scale)),
            (isoPos.y + (currentTextureRegion.regionHeight * scale / 2)),
            scale
        )
        //Debug thing
        //shapeDrawer.filledCircle(isoPos, .2f, Color.GREEN)
    }
}


