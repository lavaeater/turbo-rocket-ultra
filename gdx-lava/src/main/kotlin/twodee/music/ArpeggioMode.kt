package twodee.music

sealed class ArpeggioMode(val name: String) {
    object Up: ArpeggioMode("Up")
    object Down: ArpeggioMode("Down")
    object Random: ArpeggioMode("Random")
}
