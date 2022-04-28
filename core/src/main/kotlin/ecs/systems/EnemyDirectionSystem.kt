package ecs.systems

import ai.enemy.EnemyState
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.CharacterSpriteComponent
import ecs.components.EnemyComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.Mappers
import tru.AnimState
import tru.SpriteDirection

class EnemyDirectionSystem : IteratingSystem(allOf(CharacterSpriteComponent::class, EnemyComponent::class).get()) {
    var characterAngle = 0f
    val enemyMapper = mapperFor<EnemyComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /**
         * The difficult thing here is how we know if a character is walking or not... and what the character
         * is "doing" currently.
         *
         * This system does one, and only one thing: sets the direction the character should be
         * facing.
         *
         * The current anim is managed by something else... the input system, obviously
         * The current anim is managed by something else... the input system, obviously
         */
        val characterSpriteComponent = Mappers.characterSpriteComponentMapper.get(entity)
        val enemyComponent = enemyMapper.get(entity)
        characterAngle = if(enemyComponent.state == EnemyState.Seeking) enemyComponent.scanVector.angleDeg() else enemyComponent.directionVector.angleDeg()
        characterSpriteComponent.currentAnimState = if(enemyComponent.state == EnemyState.Seeking) AnimState.Idle else AnimState.Walk

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