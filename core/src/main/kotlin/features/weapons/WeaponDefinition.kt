package features.weapons

/**
 *
 * @param ROF is in shots per minute
 */
class WeaponDefinition(
    val name: String,
    val ammoCap: Int,
    val rof: Float,
    val accuracy: Float,
    val numberOfProjectiles: Int,
    val maxSpread: Float,
    val textureName: String
) {
    companion object {
        val weapons = listOf(
            WeaponDefinition(
                "Glock 17",
                17,
                60f,
                5f,
                1,
                0f,
                GunFrames.handGun
            ),
            WeaponDefinition(
                "Franchi Spas 12",
                10,
                30f,
                5f,
                8,
                30f,
                GunFrames.spas12
            )
        )
    }
    fun getWeapon(): Weapon {
        return Weapon(this)
    }
}