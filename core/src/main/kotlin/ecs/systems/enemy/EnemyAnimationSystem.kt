package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.AgentProperties
import eater.physics.getComponent
import ecs.components.enemy.AttackableProperties
import ecs.components.graphics.AnimatedCharacterComponent
import ktx.ashley.allOf
import physics.*
import tru.AnimState
import eater.input.CardinalDirection

class EnemyAnimationSystem : IteratingSystem(
    allOf(
        AnimatedCharacterComponent::class, AgentProperties::class, AttackableProperties::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val characterSpriteComponent = AshleyMappers.animatedCharacter.get(entity)
        characterSpriteComponent.currentAnimState = entity.enemyAnimState()
        characterSpriteComponent.currentDirection = entity.enemyDirection()
    }
}

fun Entity.enemyDirection() : CardinalDirection {
    val characterAngle = this.agentProps().directionVector.angleDeg()
    return if(this.getComponent<AttackableProperties>().isDead)
        CardinalDirection.South
    else
        when (characterAngle) {
            in 150f..209f -> CardinalDirection.East
            in 210f..329f -> CardinalDirection.North
            in 330f..360f -> CardinalDirection.West
            in 0f..29f -> CardinalDirection.West
            in 30f..149f -> CardinalDirection.South
            else -> CardinalDirection.South
        }
}
fun Entity.enemyAnimState(): AnimState {
    //This must be set by the leaf tasks from now on!
    //TODO: Fix this
    val speed = this.agentProps().speed
    return if(this.getComponent<AttackableProperties>().isDead) AnimState.Death else if(speed == 0f) AnimState.Idle else AnimState.Walk
}