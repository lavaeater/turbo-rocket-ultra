package gamestate


sealed class GameState(val name: String) {
    object Splash: GameState("Splash")
    object Setup: GameState("Setup")
    object Running: GameState("Running")
    object Paused: GameState("Paused")
    object Ended: GameState("Ended")
    object Editor: GameState("Editor")
    object Concept: GameState("Concept")
    object MapEditor: GameState("MapEditor")

    override fun toString(): String {
        return name
    }
}

