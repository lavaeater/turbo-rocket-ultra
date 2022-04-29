package story.conversation

class ConversationStep(
    val key:String,
    val antagonistLines: Iterable<String>,
    val conversationRoutes: Iterable<ConversationRoute>)