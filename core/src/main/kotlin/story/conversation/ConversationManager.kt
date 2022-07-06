package story.conversation

import eater.injection.InjectionContext.Companion.inject
import gamestate.GameEvent
import gamestate.GameState
import statemachine.StateMachine
import wastelandui.IUserInterface

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

