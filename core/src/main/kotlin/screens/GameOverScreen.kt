package screens

import gamestate.GameEvent
import gamestate.GameState
import ktx.app.KtxScreen
import statemachine.StateMachine

class GameOverScreen(gameState: StateMachine<GameState, GameEvent>) : UserInterfaceScreen(gameState) {

}
