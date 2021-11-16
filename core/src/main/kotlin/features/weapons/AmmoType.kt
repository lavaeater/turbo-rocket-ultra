package features.weapons

sealed class AmmoType(val name: String) {
    object NineMilliMeters: AmmoType("9mm")
    object TwelveGaugeShotgun: AmmoType("12gauge")
    object FnP90Ammo: AmmoType("FN 5.7x28mm")
    object MeleeWeapon: AmmoType("N/A")

    override fun toString(): String {
        return name
    }
}