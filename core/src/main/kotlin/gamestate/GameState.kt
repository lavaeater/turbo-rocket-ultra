package gamestate


sealed class GameState {
    object Splash: GameState()
    object Setup: GameState()
    object Running: GameState()
    object Paused: GameState()
    object Ended: GameState()
}

