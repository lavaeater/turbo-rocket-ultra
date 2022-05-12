package ecs.components.graphics

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

class AnimatedCharacterComponent: Component, Pool.Poolable {
    var anims: Map<AnimState, LpcCharacterAnim<Sprite>> = emptyMap()
    var currentDirection: SpriteDirection = SpriteDirection.South
    var currentAnimState: AnimState = AnimState.Idle
    lateinit var currentAnim : Animation<Sprite>
    override fun reset() {
        anims = emptyMap()
        currentAnimState = AnimState.Idle
    }
}