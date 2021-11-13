package gamestate

sealed class GameEvent {
    object LeftSplash: GameEvent()
    object StartedGame: GameEvent()
    object PausedGame: GameEvent()
    object ResumedGame: GameEvent()
    object ExitedGame: GameEvent()
    object GameOver: GameEvent()
    object RestartGame : GameEvent()
    object StartEditor : GameEvent()
    object StopEditor : GameEvent()
    object StartConcept : GameEvent()
    object StopConcept : GameEvent()
    object DialogEvent : GameEvent()
}