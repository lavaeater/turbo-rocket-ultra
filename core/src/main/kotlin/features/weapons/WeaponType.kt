package features.weapons

sealed class WeaponType(val name: String) {
    object Projectile : WeaponType("Projectile")
    object ThrownWeapon : WeaponType("ThrownWeapon")
    object Melee : WeaponType("Melee")

    override fun toString(): String {
        return name
    }
}