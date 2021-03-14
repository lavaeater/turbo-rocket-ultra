package ecs.components

import com.badlogic.ashley.core.Component
import tru.AnimState
import tru.LpcCharacterAnim
import tru.SpriteDirection

class CharacterSpriteComponent(val anims: Map<AnimState, LpcCharacterAnim>) : Component {
    var currentAnimState : AnimState = anims.values.first().state
    val currentAnim : LpcCharacterAnim = anims[currentAnimState]!!
    var currentDirection: SpriteDirection = SpriteDirection.South
}