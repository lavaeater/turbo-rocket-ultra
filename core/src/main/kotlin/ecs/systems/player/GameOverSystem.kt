package ecs.systems.player

import com.badlogic.ashley.systems.IntervalSystem
import ecs.components.enemy.AttackableProperties
import ecs.components.player.PlayerComponent
import gamestate.GameEvent
import gamestate.GameState
import ktx.ashley.allOf
import physics.getComponent
import statemachine.StateMachine

class GameOverSystem(private val gameState: StateMachine<GameState, GameEvent>) : IntervalSystem(1f) {

    private val playerFamily = allOf(PlayerComponent::class).get()
    private val players get() = engine.getEntitiesFor(playerFamily)

    override fun updateInterval() {
        if(players.map { it.getComponent<PlayerComponent>() }.all { it.player.lives < 1 && it.player.entity.getComponent<AttackableProperties>().isDead })
            gameState.acceptEvent(GameEvent.GameOver)
    }
}