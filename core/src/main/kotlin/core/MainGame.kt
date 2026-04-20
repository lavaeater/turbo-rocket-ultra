package core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import data.Players
import de.eskalon.commons.screen.ManagedScreen
import de.eskalon.commons.screen.ScreenManager
import de.eskalon.commons.screen.transition.ScreenTransition
import de.eskalon.commons.screen.transition.impl.BlendingTransition
import de.eskalon.commons.screen.transition.impl.PushTransition
import de.eskalon.commons.screen.transition.impl.SlidingDirection
import de.eskalon.commons.screen.transition.impl.SlidingInTransition
import de.eskalon.commons.screen.transition.impl.SlidingOutTransition
import de.eskalon.commons.utils.BasicInputMultiplexer
import dependencies.InjectionContext
import dependencies.InjectionContext.Companion.inject
import gamestate.GameEvent
import gamestate.GameState
import dependencies.Context
import ktx.assets.DisposableContainer
import ktx.assets.DisposableRegistry
import ktx.inject.register
import ktx.log.debug
import screens.*
import statemachine.StateMachine
import animation.Assets
import turbofacts.FactPersistence
import turbofacts.Factoids
import turbofacts.factsOfTheWorld

class MainGame : ApplicationAdapter(), DisposableRegistry by DisposableContainer() {

    val screenManager = ScreenManager<ManagedScreen, ScreenTransition>()
    private val inputMultiplexer = BasicInputMultiplexer()

    val gameState: StateMachine<GameState, GameEvent> by lazy { inject() }

    // Screens — created lazily so OpenGL context is ready
    private val splashScreen by lazy { SplashScreen(gameState) }
    private val setupScreen by lazy { SetupScreen(gameState) }
    private val gameScreen by lazy { GameScreen(gameState) }
    private val pauseScreen by lazy { PauseScreen(gameState) }
    private val gameOverScreen by lazy { GameOverScreen(gameState) }
    private val animEditorScreen by lazy { AnimEditorScreen(gameState) }
    private val behaviorTreeScreen by lazy { BehaviorTreeViewScreen(gameState) }
    private val conceptScreen by lazy { ConceptScreen(gameState) }
    private val mapEditorScreen by lazy { MapEditorScreen(gameState) }
    private val characterEditorScreen by lazy { CharacterEditorScreen(gameState) }
    private val mutatorArenaScreen by lazy { MutatorArenaScreen(gameState) }

    // Shared batch for all BatchTransitions (not disposed by transitions)
    private lateinit var transitionBatch: SpriteBatch

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

    // Push a screen with a given transition
    private fun push(screen: ManagedScreen, transition: ScreenTransition? = null) {
        screenManager.pushScreen(screen, transition)
    }

    override fun create() {
        transitionBatch = SpriteBatch()

        Context.initializeContext()
        InjectionContext.context.register {
            bindSingleton(StateMachine.buildStateMachine<GameState, GameEvent>(GameState.Splash, ::stateChanged) {
                state(GameState.Splash) {
                    action { push(splashScreen) }
                    edge(GameEvent.LeftSplash, GameState.Setup) {}
                }
                state(GameState.Setup) {
                    action { push(setupScreen, BlendingTransition(transitionBatch, 0.4f)) }
                    edge(GameEvent.StartedGame, GameState.Running) {
                        action { resetPlayers() }
                    }
                    edge(GameEvent.StartAnimEditor, GameState.AnimEditor) {}
                    edge(GameEvent.StartConcept, GameState.Concept) {}
                    edge(GameEvent.StartMapEditor, GameState.MapEditor) {}
                    edge(GameEvent.StartCharacterEditor, GameState.CharacterEditor) {}
                    edge(GameEvent.StartMutatorArena, GameState.MutatorArena) {}
                }
                state(GameState.Running) {
                    action {
                        push(gameScreen, PushTransition(transitionBatch, SlidingDirection.LEFT, 0.4f, Interpolation.sine))
                        gameScreen.resume()
                    }
                    edge(GameEvent.PausedGame, GameState.Paused) {}
                    edge(GameEvent.StartedConversation, GameState.Conversation) {}
                    edge(GameEvent.GameOver, GameState.Setup) {
                        action {}
                    }
                }
                state(GameState.Paused) {
                    action { gameScreen.pause() }
                    edge(GameEvent.ResumedGame, GameState.Running) {}
                    edge(GameEvent.ExitedGame, GameState.Setup) {}
                }
                state(GameState.Conversation) {
                    action { gameScreen.pause() }
                    edge(GameEvent.DialogEvent, GameState.Running) {
                        action { gameScreen.resume() }
                    }
                }
                state(GameState.AnimEditor) {
                    action { push(animEditorScreen, SlidingInTransition(transitionBatch, SlidingDirection.RIGHT, 0.35f)) }
                    edge(GameEvent.StopAnimEditor, GameState.Setup) {}
                }
                state(GameState.CharacterEditor) {
                    action { push(characterEditorScreen, SlidingInTransition(transitionBatch, SlidingDirection.UP, 0.35f)) }
                    edge(GameEvent.StopCharacterEditor, GameState.Setup) {}
                }
                state(GameState.Concept) {
                    action { push(conceptScreen, BlendingTransition(transitionBatch, 0.3f, Interpolation.fade)) }
                    edge(GameEvent.StopConcept, GameState.Setup) {}
                }
                state(GameState.MapEditor) {
                    action { push(mapEditorScreen, SlidingInTransition(transitionBatch, SlidingDirection.DOWN, 0.35f)) }
                    edge(GameEvent.ExitMapEditor, GameState.Setup) {}
                }
                state(GameState.MutatorArena) {
                    action { push(mutatorArenaScreen, SlidingInTransition(transitionBatch, SlidingDirection.UP, 0.4f, Interpolation.swing)) }
                    edge(GameEvent.ExitMutatorArena, GameState.Setup) {}
                }
            })
        }

        val facts = factsOfTheWorld()
        FactPersistence.load(facts)
        facts.setBooleanFact(FactPersistence.saveExists(), Factoids.SaveExists)

        Assets.load().alsoRegister()

        screenManager.initialize(inputMultiplexer, Gdx.graphics.width, Gdx.graphics.height, false)
        Gdx.input.inputProcessor = inputMultiplexer

        gameState.initialize()
    }

    override fun render() {
        screenManager.render(Gdx.graphics.deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        screenManager.resize(width, height)
    }

    override fun pause() {
        screenManager.pause()
    }

    override fun resume() {
        screenManager.resume()
    }

    override fun dispose() {
        FactPersistence.save(factsOfTheWorld())
        screenManager.dispose()
        transitionBatch.dispose()
        super.dispose()
    }
}
