package features.weapons

sealed class AmmoType(val name: String) {
    object nineMilliMeters: AmmoType("9mm")
    object twelveGaugeShotgun: AmmoType("12gauge")
    object fnP90Ammo: AmmoType("FN 5.7x28mm")

    override fun toString(): String {
        return name
    }
}