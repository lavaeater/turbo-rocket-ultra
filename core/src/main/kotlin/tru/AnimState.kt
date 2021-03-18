package tru

sealed class AnimState() {
    object Idle : AnimState()
    object Walk : AnimState()
    object StartAim : AnimState()
    object Aiming : AnimState()
    object Death : AnimState()
}