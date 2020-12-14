object Context {
    private val context = Context()
    init {
        context.register {
            bindSingleton
        }

    }
}