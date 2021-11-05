package input

sealed class Button(val playstationButtonName: String) {
    object Green : Button("X")
    object Red : Button("Dont know")
    object Blue : Button("SQUARE")
    object Yellow : Button("Dont know")
    object DPadLeft : Button("Dpadleft - maybe not, it might be an axis?")
    object DPadRight : Button("DpadRight")
    object Unknown : Button("Unknown")

    companion object {
        fun getButton(buttonCode: Int): Button {
            return when (buttonMap.containsKey(buttonCode)) {
                true -> buttonMap[buttonCode]!!
                false -> Unknown
            }
        }

        private val buttonMap = mapOf(
            0 to Green,
            1 to Red,
            2 to Blue,
            3 to Yellow,
            13 to DPadLeft,
            14 to DPadRight
        )
    }

}