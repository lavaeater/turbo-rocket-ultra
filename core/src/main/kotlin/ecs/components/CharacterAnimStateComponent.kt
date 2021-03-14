package ecs.components

import tru.AnimState
import tru.SpriteDirection
import java.awt.Component

class CharacterAnimStateComponent(var currentAnimState : AnimState = AnimState.Idle, var currentDirection: SpriteDirection.South) : Component {
}