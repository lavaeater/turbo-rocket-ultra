package messaging

import com.badlogic.gdx.utils.Queue
import kotlin.reflect.KClass

class MessageHandler {
    // QUeue not used for now, but perhaps later, much later?
    val messageQueue = Queue<Message>()
    val receivers = mutableListOf<MessageReceiver>()
    fun sendMessage(message: Message) {
        val validReceivers = receivers.filter { it.messageTypes.contains<KClass<out Any>>(message::class) }
        for (receiver in validReceivers) {
            receiver.receiveMessage(message)
        }
    }
}