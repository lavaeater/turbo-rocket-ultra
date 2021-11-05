package features.weapons

/**
 *
 * @param ROF is in shots per minute
 */
class GunDefinition(
    val name: String,
    val ammoCap: Int,
    val rof: Float,
    val accuracy: Float,
    val numberOfProjectiles: Int,
    val maxSpread: Float,
    val textureName: String,
    val ammoType: AmmoType
) {
    companion object {
        val guns = listOf(
            GunDefinition(
                "Glock 17",
                300,
                60f,
                5f,
                1,
                0f,
                GunFrames.handGun,
                AmmoType.nineMilliMeters
            ),
            GunDefinition(
                "Franchi Spas 12",
                300,
                30f,
                5f,
                8,
                30f,
                GunFrames.spas12,
                AmmoType.twelveGaugeShotgun
            )
        )
    }
    fun getGun(): Gun {
        return Gun(this)
    }
}