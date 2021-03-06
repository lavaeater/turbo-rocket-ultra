package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.ai.AttackPlayer
import ecs.components.ai.PlayerIsInRange
import ecs.components.ai.TrackingPlayerComponent
import ecs.components.player.PlayerRespawning
import ecs.components.player.PlayerWaitsForRespawn
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.ashley.remove
import ktx.math.random
import ktx.math.vec2
import physics.hasComponent

class AttackPlayerSystem : IteratingSystem(allOf(
    AttackPlayer::class,
    EnemyComponent::class,
    TransformComponent::class,
    TrackingPlayerComponent::class).get()) {
    private val mapper = mapperFor<AttackPlayer>()
    private val tMapper = mapperFor<TransformComponent>()
    private val trackerMapper = mapperFor<TrackingPlayerComponent>()

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attackPlayer = mapper[entity]
        val transformComponent = tMapper[entity]
        val player = trackerMapper[entity].player!!

        if(attackPlayer.status == Task.Status.RUNNING) {
            if(player.entity.hasComponent<PlayerWaitsForRespawn>()) {
                //Can't attack invisible / dead player
                attackPlayer.status = Task.Status.FAILED
                entity.remove<TrackingPlayerComponent>()
                return
            }

            if(attackPlayer.coolDown <= 0f) {
                attackPlayer.coolDown = attackPlayer.coolDownRange.random()//This guy needs to wait a little before attacking again.
                if((1..3).random() == 1 && !player.entity.hasComponent<PlayerRespawning>()) {
                    player.health -= (5..15).random()
                }
            } else {
                attackPlayer.coolDown -= deltaTime
                val playerPosition = tMapper.get(player.entity).position
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