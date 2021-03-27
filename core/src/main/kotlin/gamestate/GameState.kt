package gamestate

import statemachine.StateMachine

fun getStateMachine(stateChanged: (GameState)->Unit) : StateMachine<GameState, GameEvent> {
    return StateMachine.buildStateMachine<GameState, GameEvent>(GameState.Splash, stateChanged) {
        state(GameState.Splash) {
            edge(GameEvent.LeftSplash, GameState.Setup) {}
        }
        state(GameState.Setup) {
            edge(GameEvent.StartedGame, GameState.Running) {}
        }
        state(GameState.Running) {
            edge(GameEvent.PausedGame, GameState.Paused) {}
            edge(GameEvent.GameOver, GameState.Ended) {

            }
        }
        state(GameState.Paused) {
            edge(GameEvent.ResumedGame, GameState.Running) {}
            edge(GameEvent.ExitedGame, GameState.Setup) {}
        }
        state(GameState.Ended) {
            edge(GameEvent.ExitedGame, GameState.Setup) {}
            edge(GameEvent.RestartGame, GameState.Running) {}
        }
    }
}

sealed class GameState {
    object Splash: GameState()
    object Setup: GameState()
    object Running: GameState()
    object Paused: GameState()
    object Ended: GameState()
}

sealed class GameEvent {
    object LeftSplash: GameEvent()
    object StartedGame: GameEvent()
    object PausedGame: GameEvent()
    object ResumedGame: GameEvent()
    object ExitedGame: GameEvent()
    object GameOver: GameEvent()
    object RestartGame : GameEvent()
}