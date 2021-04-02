package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.AttackPlayer
import ecs.components.ai.ChasePlayer
import ecs.components.ai.TrackingPlayerComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.graphics.RenderableComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.PlayerIsDead
import ecs.components.player.PlayerRespawning
import ecs.components.player.PlayerWaitsForRespawn
import gamestate.GameEvent
import gamestate.GameState
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.ashley.remove
import physics.getComponent
import physics.hasComponent
import statemachine.StateMachine

class GameOverSystem(private val gameState: StateMachine<GameState, GameEvent>) : IntervalSystem(1f) {

    private val playerFamily = allOf(PlayerComponent::class).get()
    private val players get() = engine.getEntitiesFor(playerFamily)

    @ExperimentalStdlibApi
    override fun update() {
        if(players.map { it.getComponent<PlayerComponent>() }.all { it.player.lives < 1 && it.player.isDead })
            gameState.acceptEvent(GameEvent.GameOver)
    }
}

class PlayerDeathSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    val mapper = mapperFor<PlayerComponent>()
    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pc = mapper[entity]
        if(pc.player.isDead && pc.player.lives > 0 && !entity.hasComponent<PlayerWaitsForRespawn>() && !entity.hasComponent<PlayerRespawning>()) {
            pc.player.lives -= 1
            entity.add(engine.createComponent(PlayerWaitsForRespawn::class.java))
        } else if(pc.player.isDead && pc.player.lives <= 0){
            entity.add(engine.createComponent(PlayerIsDead::class.java))
        }

        if(entity.hasComponent<PlayerIsDead>() || entity.hasComponent<PlayerWaitsForRespawn>()) {
            entity.remove<RenderableComponent>()
            for(enemy in engine.getEntitiesFor(allOf(EnemyComponent::class).get())) {
                if(enemy.hasComponent<TrackingPlayerComponent>() && enemy.getComponent<TrackingPlayerComponent>().player == pc.player) {
                    enemy.remove<ChasePlayer>()
                    enemy.remove<AttackPlayer>()
                    enemy.remove<TrackingPlayerComponent>()
                }
            }
        }

        if(entity.hasComponent<PlayerWaitsForRespawn>()) {
            val dc = entity.getComponent<PlayerWaitsForRespawn>()
            dc.coolDown-= deltaTime
            if(dc.coolDown < 0f) {
                entity.remove(PlayerWaitsForRespawn::class.java)
                entity.add(engine.createComponent(PlayerRespawning::class.java))
                pc.player.health = 100
            }
        }
        if(entity.hasComponent<PlayerRespawning>()) {
            if(!entity.hasComponent<RenderableComponent>()) {
                entity.add(engine.createComponent(RenderableComponent::class.java).apply {
                    layer = 1
                })
            }
            val rc = entity.getComponent<PlayerRespawning>()
            rc.coolDown-= deltaTime
            if(rc.coolDown < 0f) {
                entity.remove(PlayerRespawning::class.java)
            }
        }

    }
}