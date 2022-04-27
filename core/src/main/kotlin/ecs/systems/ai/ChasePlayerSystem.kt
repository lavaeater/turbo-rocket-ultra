package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.ChasePlayer
import ecs.components.ai.PlayerIsInRange
import ecs.components.ai.TrackingPlayer
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.math.vec2
import physics.addComponent
import physics.getComponent

class ChasePlayerSystem: IteratingSystem(allOf(
    ChasePlayer::class,
    EnemyComponent::class,
    TransformComponent::class,
    TrackingPlayer::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val chasePlayer = entity.getComponent<ChasePlayer>()
        if(chasePlayer.status == Task.Status.RUNNING) {
            chasePlayer.coolDown -= deltaTime


            val enemyComponent = entity.getComponent<EnemyComponent>()
            val transformComponent = entity.getComponent<TransformComponent>()
            val playerPosition = entity.getComponent<TrackingPlayer>().player!!.entity.getComponent<TransformComponent>().position
            val distance = vec2().set(transformComponent.position).sub(playerPosition).len2()
            when {
                distance < 5f -> {
                    entity.addComponent<PlayerIsInRange>()
                    chasePlayer.status = Task.Status.SUCCEEDED
                }
                chasePlayer.coolDown > 0f -> {
                    enemyComponent.speed = 5f
                    enemyComponent.directionVector.set(playerPosition).sub(transformComponent.position)
                        .nor()
                }
                else -> {
                    enemyComponent.speed = 1f
                    chasePlayer.status = Task.Status.FAILED
                }
            }
        }
    }
}