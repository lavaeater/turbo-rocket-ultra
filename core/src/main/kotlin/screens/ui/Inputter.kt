package screens.ui

class Inputter(override val name: String, val inputMap: Map<Int, () -> Unit>) : InputThing {
    override fun handleInput(keyCode: Int): Boolean {
        if (inputMap.containsKey(keyCode)) {
            inputMap[keyCode]!!()
        }
        return true
    }
}