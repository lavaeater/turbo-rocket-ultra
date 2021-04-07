package tru

sealed class AnimState() {
    companion object {
        val animStates = listOf(Idle, Walk, StartAim, Aiming, Death)
    }
    object Idle : AnimState()
    object Walk : AnimState()
    object StartAim : AnimState()
    object Aiming : AnimState()
    object Death : AnimState()
}