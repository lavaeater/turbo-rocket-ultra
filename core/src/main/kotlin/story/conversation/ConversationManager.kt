package story.conversation

import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import statemachine.StateMachine
import ui.IUserInterface

class ConversationManager(
    private val ui: IUserInterface

) {
    private val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }
    fun startConversation(
        conversation: IConversation,
        endConversation: () -> Unit,
        showProtagonistPortrait: Boolean = true,
        showAntagonistPortrait: Boolean = true
    ) {
        ui.runConversation(
            conversation, {
                endConversation()
                gameState.acceptEvent(GameEvent.DialogEvent)
            },
            showProtagonistPortrait,
            showAntagonistPortrait
        )

    }
}

