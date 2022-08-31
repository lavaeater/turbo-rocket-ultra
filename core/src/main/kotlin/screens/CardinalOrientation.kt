package screens

sealed class CardinalOrientation(val name: String) {
    object Up : CardinalOrientation("Up")
    object Down : CardinalOrientation("Down")
    object Forward : CardinalOrientation("Forward")
    object Backwards : CardinalOrientation("Backwards")
    object Left : CardinalOrientation("Left")
    object Right : CardinalOrientation("Right")
}