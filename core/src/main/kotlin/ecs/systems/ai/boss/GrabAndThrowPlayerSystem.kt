package ecs.systems.ai.boss

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.ai.PlayerIsInRange
import ecs.components.ai.TrackingPlayer
import ecs.components.ai.boss.GrabAndThrowPlayer
import ecs.components.player.PlayerIsRespawning
import ecs.components.player.PlayerWaitsForRespawn
import ktx.ashley.allOf
import ktx.ashley.remove
import ktx.math.random
import ktx.math.vec2
import physics.getComponent
import physics.has

class GrabAndThrowPlayerSystem : IteratingSystem(allOf(
    GrabAndThrowPlayer::class,
    EnemyComponent::class,
    TransformComponent::class,
    TrackingPlayer::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attackPlayer = entity.getComponent<GrabAndThrowPlayer>()
        val transformComponent = entity.getComponent<TransformComponent>()
        val player = entity.getComponent<TrackingPlayer>().player!!

        if(attackPlayer.status == Task.Status.RUNNING) {
            if(player.entity.has<PlayerWaitsForRespawn>()) {
                //Can't attack invisible / dead player
                attackPlayer.status = Task.Status.FAILED
                entity.remove<TrackingPlayer>()
                return
            }

            if(attackPlayer.coolDown <= 0f) {
                attackPlayer.coolDown = attackPlayer.coolDownRange.random()//This guy needs to wait a little before attacking again.
                if((1..3).random() == 1 && !player.entity.has<PlayerIsRespawning>()) {
                    player.health -= (5..15).random()
                }
            } else {
                attackPlayer.coolDown -= deltaTime
                val playerPosition = player.entity.getComponent<TransformComponent>().position
                val distance = vec2().set(transformComponent.position).sub(playerPosition).len2()
                if (distance > 5f) {
                    entity.remove<PlayerIsInRange>()
                    //player has moved away, we have failed
                    attackPlayer.status = Task.Status.FAILED
                }
            }
        }
    }
}