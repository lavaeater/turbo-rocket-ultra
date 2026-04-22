package screens

import gamestate.GameEvent
import gamestate.GameState
import ktx.actors.stage
import screens.basic.BasicScreen
import statemachine.StateMachine

abstract class UserInterfaceScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    protected val stage by lazy { stage(batch, viewport) }
    override fun render(delta: Float) {
        super.render(delta)
        stage.act(delta)
        stage.draw()
    }
}