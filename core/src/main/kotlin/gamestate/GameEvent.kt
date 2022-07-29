package gamestate

sealed class GameEvent(val name: String) {
    object LeftSplash: GameEvent("LeftSplash")
    object StartedGame: GameEvent("StartedGame")
    object PausedGame: GameEvent("PausedGame")
    object ResumedGame: GameEvent("ResumedGame")
    object ExitedGame: GameEvent("ExitedGame")
    object GameOver: GameEvent("GameOver")
    object RestartGame : GameEvent("RestartGame")
    object StartCharacterEditor : GameEvent("StartEditor")
    object StopCharacterEditor : GameEvent("StopEditor")
    object StartConcept : GameEvent("StartConcept")
    object StopConcept : GameEvent("StopConcept")
    object ExitMapEditor : GameEvent("ExitMapEditor")
    object StartMapEditor : GameEvent("StartMapEditor")
    object DialogEvent : GameEvent("DialogEvent")
    object LevelComplete : GameEvent("LevelComplete")
    object StartAnimEditor: GameEvent("StartAnimEditor")
    object StopAnimEditor: GameEvent("StopAnimEditor")

    override fun toString(): String {
        return name
    }
}