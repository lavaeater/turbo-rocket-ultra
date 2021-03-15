package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

class CharacterSpriteComponent(private val anims: Map<AnimState, LpcCharacterAnim>) : Component {
    var currentAnimState : AnimState = anims.values.first().state
    var currentDirection: SpriteDirection = SpriteDirection.South
    val currentAnim : Animation<TextureRegion> get() = anims[currentAnimState]!!.animations[currentDirection]!!
}