package features.weapons

sealed class WeaponType {
    object Projectile : WeaponType()
    object ThrownArea : WeaponType()
    object Melee : WeaponType()
}