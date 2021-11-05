package features.weapons

sealed class AmmoType(val name: String) {
    object nineMilliMeters: AmmoType("9mm")
    object twelveGaugeShotgun: AmmoType("12gauge")
}