package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.*
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.math.vec2
import physics.addComponent
import physics.enemy
import physics.getComponent
import physics.transform

class AlertFriendsSystem: IteratingSystem(allOf(
    AlertFriends::class,
    EnemyComponent::class,
    TransformComponent::class,
    IsAwareOfPlayer::class,
    KnownPosition::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        /*
        Alert enemies by running to them and when bumping into them handling
        that event, somehow
         */

        val chasePlayer = entity.getComponent<ChasePlayer>()
        if(chasePlayer.status == Task.Status.RUNNING) {
            chasePlayer.coolDown -= deltaTime

            val enemyComponent = entity.enemy()
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