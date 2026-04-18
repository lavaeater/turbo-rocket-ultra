package story.consequence

import dependencies.InjectionContext.Companion.inject
import story.conversation.ConversationManager
import story.conversation.IConversation

class ConversationConsequence(
    private val conversation: IConversation,
    private val afterConversation: () -> Unit = {}
) : Consequence {
    override val consequenceType = ConsequenceType.ConversationLoader

    private val conversationManager by lazy { inject<ConversationManager>() }

    override fun apply() {
        conversationManager.startConversation(conversation, afterConversation)
    }
}
