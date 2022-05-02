package gamestate

import data.Players
import factories.factsOfTheWorld
import injection.Context
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.register
import screens.*
import statemachine.StateMachine
import tru.Assets
import turbofacts.Factoids

class MainGame : KtxGame<KtxScreen>() {

    val gameState: StateMachine<GameState, GameEvent> by lazy {
        val stateMachine = StateMachine.buildStateMachine<GameState, GameEvent>(GameState.Splash, ::stateChanged) {
            state(GameState.Splash) {
                action { setScreen<SplashScreen>() }
                edge(GameEvent.LeftSplash, GameState.Setup) {}
            }
            state(GameState.Setup) {
                action { setScreen<SetupScreen>() }
                edge(GameEvent.StartedGame, GameState.Running) {
                    action {
                        resetPlayers()
                    }
                }
                edge(GameEvent.StartEditor, GameState.Editor) {}
                edge(GameEvent.StartConcept, GameState.Concept) {}
                edge(GameEvent.StartMapEditor, GameState.MapEditor) {}
            }
            state(GameState.Running) {
                action {
                    setScreen<GameScreen>()
                    gameScreen.resume()
                }
                edge(GameEvent.PausedGame, GameState.Paused) {}
                edge(GameEvent.GameOver, GameState.Setup) {}
                edge(GameEvent.LevelComplete, GameState.Setup) {}
            }
            state(GameState.Paused) {
                action {
                    gameScreen.pause()
                }
                edge(GameEvent.ResumedGame, GameState.Running) {}
                edge(GameEvent.ExitedGame, GameState.Setup) {}
            }
            state(GameState.Editor) {
                action { setScreen<CharacterEditorScreen>() }
                edge(GameEvent.StopEditor, GameState.Setup) {}
            }
            state(GameState.Concept) {
                action { setScreen<ConceptScreen>() }
                edge(GameEvent.StopConcept, GameState.Setup) {}
            }
            state(GameState.MapEditor) {
                action { setScreen<MapEditorScreen>() }
                edge(GameEvent.ExitMapEditor, GameState.Setup) {}
            }
        }
        Context.context.register {
            bindSingleton(stateMachine)
        }
        stateMachine
    }

    private fun resetPlayers() {
        factsOfTheWorld().setIntFact(0, Factoids.LivingPlayerCount)
        for (player in Players.players.values) {
            player.reset()
            factsOfTheWorld().addToInt(1, Factoids.LivingPlayerCount)
        }
    }

    private fun stateChanged(gameState: GameState) {

    }

    private val gameScreen by lazy {
        GameScreen(gameState)
    }

    override fun create() {
        Assets.load()
        addScreen(SplashScreen(gameState))
        addScreen(SetupScreen(gameState))
        addScreen(gameScreen)
        addScreen(PauseScreen(gameState))
        addScreen(GameOverScreen(gameState))
        addScreen(AnimEditorScreen(gameState))
        addScreen(ConceptScreen(gameState))
        addScreen(CharacterEditorScreen(gameState))
        addScreen(MapEditorScreen(gameState))
        gameState.initialize()
    }
}