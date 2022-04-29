package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.AttackPlayer
import ecs.components.ai.SeekPlayer
import ecs.components.enemy.EnemyComponent
import ecs.components.graphics.AnimatedCharacterComponent
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.getComponent
import physics.has
import tru.AnimState
import tru.SpriteDirection

class EnemyDirectionSystem : IteratingSystem(
    allOf(
        AnimatedCharacterComponent::class, EnemyComponent::class).get()) {
    var characterAngle = 0f
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val characterSpriteComponent = AshleyMappers.animatedCharacter.get(entity)
        val enemyComponent = AshleyMappers.enemy.get(entity)
        val hasSeek = AshleyMappers.seekPlayer.has(entity)
        characterAngle = if(hasSeek) AshleyMappers.seekPlayer.get(entity).scanVector.angleDeg() else enemyComponent.directionVector.angleDeg()

        characterSpriteComponent.currentAnimState = if(enemyComponent.isDead) AnimState.Death else if(hasSeek) AnimState.Idle else if(AshleyMappers.attackPlayer.has(entity)) AnimState.Slash else AnimState.Walk
        if(enemyComponent.isDead)
            characterSpriteComponent.currentDirection = SpriteDirection.South
        else
            when (characterAngle) {
                in 150f..209f -> characterSpriteComponent.currentDirection = SpriteDirection.East
                in 210f..329f -> characterSpriteComponent.currentDirection = SpriteDirection.North
                in 330f..360f -> characterSpriteComponent.currentDirection = SpriteDirection.West
                in 0f..29f -> characterSpriteComponent.currentDirection = SpriteDirection.West
                in 30f..149f -> characterSpriteComponent.currentDirection = SpriteDirection.South
                else -> characterSpriteComponent.currentDirection = SpriteDirection.South
            }
    }
}