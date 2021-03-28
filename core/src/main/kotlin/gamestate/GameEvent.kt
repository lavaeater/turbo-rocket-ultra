package gamestate

sealed class GameEvent {
    object LeftSplash: GameEvent()
    object StartedGame: GameEvent()
    object PausedGame: GameEvent()
    object ResumedGame: GameEvent()
    object ExitedGame: GameEvent()
    object GameOver: GameEvent()
    object RestartGame : GameEvent()
}