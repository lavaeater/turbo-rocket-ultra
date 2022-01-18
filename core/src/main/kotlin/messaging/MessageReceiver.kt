package messaging

import kotlin.reflect.KClass

interface MessageReceiver {
    val messageTypes: Set<KClass<*>>
    fun recieveMessage(message: Message)
}