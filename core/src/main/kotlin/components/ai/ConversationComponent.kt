package components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import story.conversation.IConversation

class ConversationComponent : Component, Pool.Poolable {
    lateinit var conversation: IConversation
    var triggerRadius: Float = 3f
    var triggeredOnce: Boolean = false
    var repeatable: Boolean = false
    var afterConversation: () -> Unit = {}

    override fun reset() {
        triggeredOnce = false
        afterConversation = {}
    }
}
