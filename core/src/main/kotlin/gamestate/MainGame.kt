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
                edge(GameEvent.StartedGame, GameState.Running) {}
            }
            state(GameState.Setup) {
                action { setScreen<SetupScreen>() }
                edge(GameEvent.StartedGame, GameState.Running) {}
            }
            state(GameState.Running) {
                action { setScreen<GameScreen>() }
                edge(GameEvent.PausedGame, GameState.Paused) {}
                edge(GameEvent.GameOver, GameState.Ended) {}
            }
            state(GameState.Paused) {
                action { setScreen<PauseScreen>() }
                edge(GameEvent.ResumedGame, GameState.Running) {}
                edge(GameEvent.ExitedGame, GameState.Setup) {}
            }
            state(GameState.Ended) {
                action { setScreen<GameOverScreen>() }
                edge(GameEvent.ExitedGame, GameState.Setup) {}
                edge(GameEvent.RestartGame, GameState.Running) {}
            }
        }

    private fun stateChanged(gameState: GameState) {

    }

    override fun create() {

        Assets.load()
        addScreen(SplashScreen(gameState))
        addScreen(GameScreen())
        addScreen(PauseScreen(gameState))
        addScreen(GameOverScreen(gameState))
        gameState.initialize()
//        setScreen<SplashScreen>()
    }
}