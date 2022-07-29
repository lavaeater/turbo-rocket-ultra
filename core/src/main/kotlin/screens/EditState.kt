package screens

sealed class EditState {
    object Normal : EditState()
    object Paint : EditState()
    object Alt : EditState()
    object Camera : EditState()
    object Command : EditState()
    object DialogMode: EditState()
}