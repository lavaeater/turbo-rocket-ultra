package turbofacts

import injection.Context.inject
import messaging.Message
import messaging.MessageHandler
import messaging.MessageReceiver
import kotlin.reflect.KClass

class TurboStoryManager {
    init {
        inject<MessageHandler>().apply {
            this.addReceiver(object: MessageReceiver {
                override val messageTypes = setOf(Message.FactUpdated::class)

                override fun receiveMessage(message: Message) {
                    when(message) {
                        is Message.FactUpdated -> needsChecking = true
                        else -> {}
                    }
                }

            })
        }
    }

    private var isActive = false

    val stories = mutableListOf<TurboStory>()
    fun addStories(vararg stories: TurboStory) {
        for(story in stories) {
            addStory(story)
        }
    }

    fun activate() {
        isActive = true
        for(story in stories)
            story.initialize()
    }

    fun addStory(story: TurboStory) {
        stories.add(story)
    }
    private var needsChecking = true
    fun checkIfNeeded() {
        if (isActive && needsChecking) {
            needsChecking = false
            for(story in stories) {
                story.checkAndApplyStory()
            }
        }
    }
}

interface Builder<out T> {
    fun build(): T
}