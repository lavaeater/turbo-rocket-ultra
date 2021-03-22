package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection
import tru.StaticSpriteDefinition

class CharacterSpriteComponent(private val anims: Map<AnimState, LpcCharacterAnim>, val conditionalSpriteObjects: MutableList<ConditionalObjectSprite> = mutableListOf()) : Component {
    var currentAnimState : AnimState = anims.values.first().state
    var currentDirection: SpriteDirection = SpriteDirection.South
    val currentAnim : Animation<TextureRegion> get() = anims[currentAnimState]!!.animations[currentDirection]!!
    val objectsToDraw get() = conditionalSpriteObjects.filter { it.condition() }
}

class ConditionalObjectSprite(private val spriteDefinition: Map<SpriteDirection, TextureRegion>, val condition: () -> Boolean, private val direction: () -> SpriteDirection) {
    val currentTextureRegion get() = spriteDefinition[direction()]!!
}
