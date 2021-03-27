package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.EnemyComponent
import ecs.components.TransformComponent
import ecs.components.ai.ChasePlayer
import gamestate.Player
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec2
import screens.Players

class ChasePlayerSystem: IteratingSystem(allOf(
    ChasePlayer::class,
    EnemyComponent::class,
    TransformComponent::class,
    PlayerTrackComponent::class).get()) {
    private val mapper = mapperFor<ChasePlayer>()
    private val eMapper = mapperFor<EnemyComponent>()
    private val tMapper = mapperFor<TransformComponent>()
    private val trackerMapper = mapperFor<PlayerTrackComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val chasePlayer = mapper[entity]
        if(chasePlayer.status == Task.Status.RUNNING) {
            val enemyComponent = eMapper[entity]
            val transformComponent = tMapper[entity]
            val playerPosition = tMapper.get(trackerMapper[entity].player!!.entity).position
            val distance = vec2().set(transformComponent.position).sub(playerPosition).len2()
            if (distance < 5f)
                chasePlayer.status = Task.Status.SUCCEEDED
            else {
                enemyComponent.speed = 3.5f
                enemyComponent.directionVector.set(playerPosition).sub(transformComponent.position)
                    .nor()
            }
        }
    }
}