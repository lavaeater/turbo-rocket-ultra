package ecs.components.experimental

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.utils.Pool
import ecs.components.graphics.OffsetTextureRegion
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

/**
 * DO not make it more complicated at this juncture. Exploring using
 * other sprites and types of anim is OK, but do not imagine a completely
 * crazy system. We already have textureComponents with extras,
 * we can make an alternative AnimatedCharacterComponent
 * that uses these alternative sprites with offsets, that could work.
 *
 * Actually, the order of drawing sprites might be important, like body first
 * or head first. Going away from camera, head first, when head is facing
 * camera, we draw body first.
 *
 * For northwest, north and northeast, the head is facing away and drawn first, and we show the back-
 * sprite.
 *
 * For west and east directions, we need to flip the sprite (there is no dedicated sprite for
 * other direction).
 * South, southwest and southeast, body drawn first, front, head drawn second, front and then legs.
 *
 * They could each be their own components... or parts of  this one...
 *
 * Now I remember, I was doing a swing move, oh, back to that.
 */
class MultiTextureAndAnimation : Component, Pool.Poolable {
    var anims: Map<AnimState, LpcCharacterAnim<OffsetTextureRegion>> = emptyMap()
    var currentDirection: SpriteDirection = SpriteDirection.South
    var currentAnimState: AnimState =
        AnimState.Idle //this is nice because all states give us the same animation - except if we are moving we get walking anim




    lateinit var currentAnim : Animation<OffsetTextureRegion>
    override fun reset() {
        anims = emptyMap()
        currentAnimState = AnimState.Idle
    }
}