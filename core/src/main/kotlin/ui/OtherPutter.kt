package screens.ui

class OtherPutter(override val name: String, val handler: (Int)-> Boolean) : InputThing {
    override fun handleInput(keyCode: Int): Boolean {
        return handler(keyCode)
    }

}