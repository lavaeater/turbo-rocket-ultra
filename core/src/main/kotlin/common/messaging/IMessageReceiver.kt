package common.messaging

import kotlin.reflect.KClass

interface IMessageReceiver {
    val messageTypes: Set<KClass<*>>

    /**
     * For this system to work properly, this method
     * should return **immediately** - any updates etc
     * can be handled using flags in whatever system receives
     * the message. Exceptions might be OK, of course -
     */
    fun receiveMessage(message: IMessage)
}