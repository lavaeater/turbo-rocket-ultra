package tru

//import kotlinx.serialization.Serializable

//@Serializable
sealed class AnimState() {
    companion object {
        val animStates = listOf(Idle, Walk, StartAim, Aiming, Death, Run, Hurt, WalkWithGun, RunWithGun, PickUp, Climb, Roll, Shoot)
    }

    override fun toString(): String {
        return this::class.toString().substringAfter(".").substringAfter("$").substringBefore("@")
    }
//    @Serializable
    object Idle : AnimState()
//    @Serializable
    object Walk : AnimState()
//    @Serializable
    object Run : AnimState()
//    @Serializable
    object Hurt : AnimState()
//    @Serializable
    object WalkWithGun : AnimState()
//    @Serializable
    object RunWithGun : AnimState()
//    @Serializable
    object PickUp : AnimState()
//    @Serializable
    object Climb : AnimState()
//    @Serializable
    object Roll : AnimState()
//    @Serializable
    object Shoot : AnimState()
//    @Serializable
    object StartAim : AnimState()
//    @Serializable
    object Aiming : AnimState()
    object Slash : AnimState()
//    @Serializable
    object Death : AnimState()
}