package input

sealed class Button {
    object Green : Button()
    object Red : Button()
    object Blue : Button()
    object Yellow : Button()
    object Unknown : Button()

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
            3 to Yellow
        )
    }

}