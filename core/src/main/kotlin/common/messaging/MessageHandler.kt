package common.messaging

import com.badlogic.gdx.utils.Queue
import kotlin.reflect.KClass

class MessageHandler {
    // QUeue not used for now, but perhaps later, much later?
    val messageQueue = Queue<IMessage>()
    private val receivers = mutableListOf<IMessageReceiver>()
    fun addReceiver(receiver: IMessageReceiver) {
        receivers.add(receiver)
    }
    fun sendMessage(message: IMessage) {
        val validReceivers = receivers.filter { it.messageTypes.contains<KClass<out Any>>(message::class) }
        for (receiver in validReceivers) {
            receiver.receiveMessage(message)
        }
    }
}