package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.AgentProperties
import ecs.components.enemy.AttackableProperties
import ecs.components.graphics.AnimatedCharacterComponent
import ktx.ashley.allOf
import physics.*
import tru.AnimState
import tru.SpriteDirection

class EnemyAnimationSystem : IteratingSystem(
    allOf(
        AnimatedCharacterComponent::class, AgentProperties::class, AttackableProperties::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val characterSpriteComponent = AshleyMappers.animatedCharacter.get(entity)
        characterSpriteComponent.currentAnimState = entity.enemyAnimState()
        characterSpriteComponent.currentDirection = entity.enemyDirection()
    }
}

fun Entity.enemyDirection() : SpriteDirection {
    val characterAngle = this.agentProps().directionVector.angleDeg()
    return if(this.getComponent<AttackableProperties>().isDead)
        SpriteDirection.South
    else
        when (characterAngle) {
            in 150f..209f -> SpriteDirection.East
            in 210f..329f -> SpriteDirection.North
            in 330f..360f -> SpriteDirection.West
            in 0f..29f -> SpriteDirection.West
            in 30f..149f -> SpriteDirection.South
            else -> SpriteDirection.South
        }
}
fun Entity.enemyAnimState(): AnimState {
    //This must be set by the leaf tasks from now on!
    //TODO: Fix this
    val speed = this.agentProps().speed
    return if(this.getComponent<AttackableProperties>().isDead) AnimState.Death else if(speed == 0f) AnimState.Idle else AnimState.Walk
}