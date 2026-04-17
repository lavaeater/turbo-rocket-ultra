package screens.ui

interface InputThing {
    val name: String
    fun handleInput(keyCode: Int): Boolean
}