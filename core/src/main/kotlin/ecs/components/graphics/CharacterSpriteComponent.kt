package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import isometric.toIsometric
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

class CharacterSpriteComponent(
    private val anims: Map<AnimState, LpcCharacterAnim>) : Renderable, Component {
    var currentAnimState : AnimState = anims.values.first().state
    var currentDirection: SpriteDirection = SpriteDirection.South
    val currentAnim : Animation<TextureRegion> get() = anims[currentAnimState]!!.animations[currentDirection]!!

    override fun render(
        position: Vector2,
        rotation:Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer) {

        val currentTextureRegion = currentAnim.getKeyFrame(animationStateTime)

        val isoPos = position.toIsometric()

        batch.drawScaled(
            currentTextureRegion,
            (isoPos.x + (currentTextureRegion.regionWidth / 2 * scale)),
            (isoPos.y + (currentTextureRegion.regionHeight * scale / 5)),
            scale
        )
    }
}

