package features.weapons

sealed class WeaponType {
    object Projectile : WeaponType()
    object ThrownWeapon : WeaponType()
    object Melee : WeaponType()
}