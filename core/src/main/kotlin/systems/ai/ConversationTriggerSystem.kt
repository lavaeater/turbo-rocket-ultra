package systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import components.TransformComponent
import components.ai.ConversationComponent
import components.player.PlayerComponent
import dependencies.InjectionContext.Companion.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import physics.transform
import story.conversation.ConversationManager

class ConversationTriggerSystem : IteratingSystem(
    allOf(ConversationComponent::class, TransformComponent::class).get()
) {
    private val conversationMapper = mapperFor<ConversationComponent>()
    private val playerFamily = allOf(PlayerComponent::class, TransformComponent::class).get()
    private val conversationManager by lazy { inject<ConversationManager>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val cc = conversationMapper.get(entity)
        if (cc.triggeredOnce && !cc.repeatable) return

        val player = engine.getEntitiesFor(playerFamily).firstOrNull() ?: return
        val distance = entity.transform().position.dst(player.transform().position)

        if (distance <= cc.triggerRadius) {
            cc.triggeredOnce = true
            conversationManager.startConversation(cc.conversation)
        }
    }
}
