package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

class CharacterSpriteComponent : Renderable, Component, Pool.Poolable {

    lateinit var anims: Map<AnimState, LpcCharacterAnim>
    var currentAnimState : AnimState = anims.values.first().state
    var currentDirection: SpriteDirection = SpriteDirection.South
    private val currentAnim : Animation<TextureRegion> get() = anims[currentAnimState]!!.animations[currentDirection]!!

    override fun render(
        position: Vector2,
        rotation:Float,
        scale: Float,
        animationStateTime: Float,
        batch: Batch,
        shapeDrawer: ShapeDrawer) {

        val currentTextureRegion = currentAnim.getKeyFrame(animationStateTime)

        batch.drawScaled(
            currentTextureRegion,
            (position.x + (currentTextureRegion.regionWidth / 2 * scale)),
            (position.y + (currentTextureRegion.regionHeight * scale / 5)),
            scale
        )
    }

    override fun reset() {
        //No-op
    }
}

