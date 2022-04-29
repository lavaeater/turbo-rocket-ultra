package screens

import gamestate.GameEvent
import gamestate.GameState
import ktx.app.KtxScreen
import statemachine.StateMachine

class PauseScreen(gameState: StateMachine<GameState, GameEvent>) : UserInterfaceScreen(gameState) {

}
