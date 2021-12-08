package input

sealed class Button(val playstationButtonName: String) {
    object Cross : Button("cross")
    object Ring : Button("ring")
    object Square : Button("square")
    object Triangle : Button("triangle")
    object DPadLeft : Button("dpadleft")
    object DPadUp : Button("dpadup")
    object DPadDown : Button("dpaddown")
    object DPadRight : Button("dpadright")
    object Share : Button("share")
    object Options : Button("options")
    object PsButton : Button("psbutton")
    object L3 : Button("l3")
    object R3 : Button("r3")
    object L1 : Button("l1")
    object R1 : Button("r1")
    object Unknown : Button("Unknown")

    companion object {
        fun getButton(buttonCode: Int): Button {
            return when (codesToButtons.containsKey(buttonCode)) {
                true -> codesToButtons[buttonCode]!!
                false -> Unknown
            }
        }

        fun getButtonCode(button: Button): Int {
            return when(buttonsToCodes.containsKey(button)) {
                true -> buttonsToCodes[button]!!
                false -> -1
            }
        }

        val codesToButtons = mapOf(
            0 to Cross,
            1 to Ring,
            2 to Square,
            3 to Triangle,
            4 to Share,
            5 to PsButton,
            6 to Options,
            7 to L3,
            8 to R3,
            9 to L1,
            10 to R1,

            11 to DPadUp,
            12 to DPadDown,
            13 to DPadLeft,
            14 to DPadRight
        )

        val buttonsToCodes = codesToButtons.entries.associate { (k,v) -> v to k }.toMap()
    }

}