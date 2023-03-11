package common.injection

import ktx.inject.Context

open class InjectionContext {

    /**
     * Call this method with your context building needs.
     *
     */
    fun buildContext(init: Context.() -> Unit) {
        context.init()
    }

    companion object {
        val context = Context()
        inline fun <reified T> inject(): T {
            return context.inject()
        }

    }
}