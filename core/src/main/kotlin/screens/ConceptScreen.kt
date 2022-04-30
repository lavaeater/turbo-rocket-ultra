package screens

import com.badlogic.gdx.scenes.scene2d.Stage
import gamestate.GameEvent
import gamestate.GameState
import statemachine.StateMachine

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {
    val stage by lazy { Stage(viewport, batch) }


    override fun render(delta: Float) {
        super.render(delta)

    }
}