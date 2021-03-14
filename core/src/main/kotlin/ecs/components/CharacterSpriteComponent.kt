package ecs.components

import com.badlogic.ashley.core.Component
import tru.AnimState
import tru.LpcCharacterAnim

class CharacterSpriteComponent(private val anims: Map<AnimState, LpcCharacterAnim>) : Component {
}