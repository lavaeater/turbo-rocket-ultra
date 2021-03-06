package gamestate

import com.badlogic.gdx.Screen
import ktx.app.KtxGame
import screens.*
import statemachine.StateMachine
import tru.Assets

class MainGame : KtxGame<Screen>() {

    val gameState = StateMachine.buildStateMachine<GameState, GameEvent>(GameState.Splash, ::stateChanged) {
            state(GameState.Splash) {
                action { setScreen<SplashScreen>() }
                edge(GameEvent.LeftSplash, GameState.Setup) {}
                edge(GameEvent.StartedGame, GameState.Running) {
                    action {
                        resetPlayers()
                    }
                }
            }
            state(GameState.Setup) {
                action { setScreen<SetupScreen>() }
                edge(GameEvent.StartedGame, GameState.Running) {
                    action {
                        resetPlayers()
                    }
                }
            }
            state(GameState.Running) {
                action {
                    setScreen<GameScreen>()
                }
                edge(GameEvent.PausedGame, GameState.Paused) {

                }
                edge(GameEvent.GameOver, GameState.Setup) {

                }
            }
            state(GameState.Paused) {
                action { setScreen<PauseScreen>() }
                edge(GameEvent.ResumedGame, GameState.Running) {
                    action {  }
                }
                edge(GameEvent.ExitedGame, GameState.Setup) {}
            }
        }

    private fun resetPlayers() {
        for (player in Players.players.values) {
            player.reset()
        }
    }

    private fun stateChanged(gameState: GameState) {

    }

    override fun create() {
        Assets.load()
        addScreen(SplashScreen(gameState))
        addScreen(SetupScreen(gameState))
        addScreen(GameScreen(gameState))
        addScreen(PauseScreen(gameState))
        addScreen(GameOverScreen(gameState))
        gameState.initialize()
    }
}