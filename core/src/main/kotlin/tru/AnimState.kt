package tru

sealed class AnimState() {
    companion object {
        val animStates = listOf(Idle, Walk, StartAim, Aiming, Death)
    }

    override fun toString(): String {
        return this::class.toString().substringAfter(".").substringAfter("$").substringBefore("@")
    }
    object Idle : AnimState()
    object Walk : AnimState()
    object Run : AnimState()
    object Hurt : AnimState()
    object WalkWithGun : AnimState()
    object RunWithGun : AnimState()
    object PickUp : AnimState()
    object Climb : AnimState()
    object Roll : AnimState()
    object Shoot : AnimState()
    object StartAim : AnimState()
    object Aiming : AnimState()
    object Death : AnimState()
}