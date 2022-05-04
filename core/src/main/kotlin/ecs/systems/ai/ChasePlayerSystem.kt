package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.ChasePlayer
import ecs.components.ai.PlayerIsInRange
import ecs.components.ai.IsAwareOfPlayer
import ecs.components.enemy.AgentProperties
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.math.vec2
import physics.addComponent
import physics.agentProps
import physics.getComponent
import physics.transform

class ChasePlayerSystem: IteratingSystem(allOf(
    ChasePlayer::class,
    AgentProperties::class,
    TransformComponent::class,
    IsAwareOfPlayer::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val chasePlayer = entity.getComponent<ChasePlayer>()
        if(chasePlayer.status == Task.Status.RUNNING) {
            chasePlayer.coolDown -= deltaTime

            val enemyComponent = entity.agentProps()
            val transformComponent = entity.transform()
            val playerPosition = entity.getComponent<IsAwareOfPlayer>().player!!.entity.getComponent<TransformComponent>().position
            val distance = vec2().set(transformComponent.position).sub(playerPosition).len2()
            when {
                distance < 5f -> {
                    entity.addComponent<PlayerIsInRange>()
                    chasePlayer.status = Task.Status.SUCCEEDED
                }
                chasePlayer.coolDown > 0f -> {
                    enemyComponent.speed = 10f
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