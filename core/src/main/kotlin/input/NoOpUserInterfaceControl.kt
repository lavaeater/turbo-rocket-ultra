package input

open class NoOpUserInterfaceControl : UserInterfaceControl {

    companion object {
        val control = NoOpUserInterfaceControl()
    }

    override fun left() {

    }

    override fun right() {

    }

    override fun cancel() {

    }

    override fun select() {
    }
}