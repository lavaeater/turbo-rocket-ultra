package screens

sealed class EditEvent {
    object EnterDialogMode : EditEvent()
    object ExitDialogMode : EditEvent()
    object EnterPaintMode : EditEvent()
    object ExitPaintMode : EditEvent()
    object ExitAltMode : EditEvent()
    object EnterAltMode : EditEvent()
    object EnterCameraMode : EditEvent()
    object ExitCameraMode : EditEvent()
    object EnterCommandMode : EditEvent()
    object ExitCommandMode : EditEvent()
}