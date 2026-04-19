package gamestate


sealed class GameState(val name: String) {
    object Splash: GameState("Splash")
    object Setup: GameState("Setup")
    object Running: GameState("Running")
    object Paused: GameState("Paused")
    object Ended: GameState("Ended")
    object CharacterEditor: GameState("CharacterEditor")
    object Concept: GameState("Concept")
    object MapEditor: GameState("MapEditor")
    object AnimEditor : GameState("AnimEditor")
    object Conversation : GameState("Conversation")
    object MutatorArena : GameState("MutatorArena")

    override fun toString(): String {
        return name
    }
}

