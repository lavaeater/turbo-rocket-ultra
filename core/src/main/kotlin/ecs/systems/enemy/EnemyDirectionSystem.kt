package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.SeekPlayer
import ecs.components.enemy.EnemyComponent
import ecs.components.graphics.CharacterSpriteComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.AshleyMappers
import physics.hasComponent
import tru.AnimState
import tru.SpriteDirection

class EnemyDirectionSystem : IteratingSystem(allOf(CharacterSpriteComponent::class, EnemyComponent::class).get()) {
    var characterAngle = 0f
    val enemyMapper = mapperFor<EnemyComponent>()
    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {

        val characterSpriteComponent = AshleyMappers.characterSpriteComponentMapper.get(entity)
        val enemyComponent = enemyMapper.get(entity)
        characterAngle = if(entity.hasComponent<SeekPlayer>()) enemyComponent.scanVector.angleDeg() else enemyComponent.directionVector.angleDeg()

        //TODO: add more animstates
        characterSpriteComponent.currentAnimState = if(entity.hasComponent<SeekPlayer>()) AnimState.Idle else AnimState.Walk

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