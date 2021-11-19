package features.weapons

sealed class ReloadType {
    object EntireMag : ReloadType()
    object SingleShot : ReloadType()
    object MeleeWeapon : ReloadType()
}