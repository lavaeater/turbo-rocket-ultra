package screens

import gamestate.GameEvent
import gamestate.GameState
import statemachine.StateMachine

class PauseScreen(gameState: StateMachine<GameState, GameEvent>) : UserInterfaceScreen(gameState)
