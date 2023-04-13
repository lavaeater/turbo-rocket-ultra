package twodee.input

sealed class InputIndicator {
    object Neutral: InputIndicator()
    object Previous: InputIndicator()
    object Next: InputIndicator()
}