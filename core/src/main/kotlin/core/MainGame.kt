package core

import data.Players
import eater.injection.InjectionContext
import eater.injection.InjectionContext.Companion.inject
import gamestate.GameEvent
import gamestate.GameState
import injection.Context
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.DisposableContainer
import ktx.assets.DisposableRegistry
import ktx.inject.register
import ktx.log.debug
import screens.*
import statemachine.StateMachine
import tru.Assets
import eater.turbofacts.Factoids
import eater.turbofacts.factsOfTheWorld
import screens.animeditor.AnimEditorScreen
import screens.behavioreditor.BehaviorTreeViewScreen
import screens.charactereditor.CharacterEditorScreen
import screens.concept.InterpolationConceptScreen

class MainGame : KtxGame<KtxScreen>(), DisposableRegistry by DisposableContainer() {

    val gameState: StateMachine<GameState, GameEvent> by lazy {
        inject()
    }

    private fun resetPlayers() {
        factsOfTheWorld().setIntFact(0, Factoids.LivingPlayerCount)
        for (player in Players.players.values) {
            player.reset()
            factsOfTheWorld().addToInt(1, Factoids.LivingPlayerCount)
        }
    }

    private fun stateChanged(gameState: GameState, gameEvent: GameEvent?) {
        debug { "$gameEvent -> $gameState" }
    }

    private val gameScreen by lazy {
        GameScreen(gameState)
    }

    override fun create() {
        Context.initializeContext()
        InjectionContext.context.register {
            bindSingleton(StateMachine.buildStateMachine<GameState, GameEvent>(GameState.Splash, ::stateChanged) {
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
                    edge(GameEvent.StartAnimEditor, GameState.AnimEditor) {}
                    edge(GameEvent.StartConcept, GameState.Concept) {}
                    edge(GameEvent.StartMapEditor, GameState.MapEditor) {}
                    edge(GameEvent.StartCharacterEditor, GameState.CharacterEditor) {}
                }
                state(GameState.Running) {
                    action {
                        setScreen<GameScreen>()
                        gameScreen.resume()
                    }
                    edge(GameEvent.PausedGame, GameState.Paused) {}
                    edge(GameEvent.GameOver, GameState.Setup) {
                        action {
                            /*
                            Nice stuff. So what do we do here?

    //                         */
//                        FitnessTracker.fitnessData.sortBy { it.fitness }
//                        val lastRelevantIndex = if( FitnessTracker.fitnessData.lastIndex > 5) 5 else FitnessTracker.fitnessData.lastIndex
//                        val evolveThese = FitnessTracker.fitnessData.subList(0, lastRelevantIndex)
//                        for((index, toEvolve) in evolveThese.withIndex()) {
//                            toEvolve.bt.kryoThisBitchToDisk(index + 1)
//                        }
                        }

                    }
                }
                state(GameState.Paused) {
                    action {
                        gameScreen.pause()
                    }
                    edge(GameEvent.ResumedGame, GameState.Running) {}
                    edge(GameEvent.ExitedGame, GameState.Setup) {}
                }
                state(GameState.AnimEditor) {
                    action { setScreen<AnimEditorScreen>() }
                    edge(GameEvent.StopAnimEditor, GameState.Setup) {}
                }
                state(GameState.CharacterEditor) {
                    action { setScreen<CharacterEditorScreen>() }
                    edge(GameEvent.StopCharacterEditor, GameState.Setup) {}
                }
                state(GameState.Concept) {
                    action { setScreen<InterpolationConceptScreen>() }
                    edge(GameEvent.StopConcept, GameState.Setup) {}
                }
                state(GameState.MapEditor) {
                    action { setScreen<MapEditorScreen>() }
                    edge(GameEvent.ExitMapEditor, GameState.Setup) {}
                }
            })
        }
        Assets.load().alsoRegister()
        addScreen(SplashScreen(gameState))
        addScreen(SetupScreen(gameState))
        addScreen(gameScreen)
        addScreen(PauseScreen(gameState))
        addScreen(GameOverScreen(gameState))
        addScreen(AnimEditorScreen(gameState))
        addScreen(BehaviorTreeViewScreen(gameState))
        addScreen(InterpolationConceptScreen(gameState))
        addScreen(MapEditorScreen(gameState))
        addScreen(CharacterEditorScreen(gameState))
        gameState.initialize()
    }
}