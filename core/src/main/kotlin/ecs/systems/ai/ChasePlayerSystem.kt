package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.ai.ChasePlayer
import ecs.components.ai.PlayerIsInRange
import ecs.components.ai.TrackingPlayerComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2

class ChasePlayerSystem: IteratingSystem(allOf(
    ChasePlayer::class,
    EnemyComponent::class,
    TransformComponent::class,
    TrackingPlayerComponent::class).get()) {
    private val mapper = mapperFor<ChasePlayer>()
    private val eMapper = mapperFor<EnemyComponent>()
    private val tMapper = mapperFor<TransformComponent>()
    private val trackerMapper = mapperFor<TrackingPlayerComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val chasePlayer = mapper[entity]
        if(chasePlayer.status == Task.Status.RUNNING) {
            chasePlayer.coolDown -= deltaTime


            val enemyComponent = eMapper[entity]
            val transformComponent = tMapper[entity]
            val playerPosition = tMapper.get(trackerMapper[entity].player!!.entity).position
            val distance = vec2().set(transformComponent.position).sub(playerPosition).len2()
            when {
                distance < 5f -> {
                    entity.add(engine.createComponent(PlayerIsInRange::class.java))
                    chasePlayer.status = Task.Status.SUCCEEDED
                }
                chasePlayer.coolDown > 0f -> {
                    enemyComponent.speed = 3.5f
                    enemyComponent.directionVector.set(playerPosition).sub(transformComponent.position)
                        .nor()
                }
                else -> {
                    chasePlayer.status = Task.Status.FAILED
                }
            }
        }
    }
}