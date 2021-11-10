package features.weapons

import com.badlogic.gdx.audio.Sound
import tru.Assets

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
    val ammoType: AmmoType,
    val damageRange: IntRange,
    val reloadDelay: Float,
    val reloadType: ReloadType,
    val audio: Map<String, Sound>
) {
    companion object {
        val guns = listOf(
            GunDefinition(
                "Glock 17",
                17,
                120f,
                5f,
                1,
                .125f,
                GunFrames.handGun,
                AmmoType.nineMilliMeters,
                8..16,
                2f,
                ReloadType.EntireMag,
                Assets.gunAudio["glock17"]!!
            ),
            GunDefinition(
                "Franchi Spas 12",
                8,
                60f,
                5f,
                16,
                15f,
                GunFrames.spas12,
                AmmoType.twelveGaugeShotgun,
                12..24,
                1f,
                ReloadType.SingleShot,
                Assets.gunAudio["spas12"]!!
            ),
            GunDefinition(
                "FN P90",
                50000,
                900f,
                5f,
                1,
                1f,
                GunFrames.spas12,
                AmmoType.fnP90Ammo,
                6..14,
                2f,
                ReloadType.EntireMag,
                Assets.gunAudio["fnp90"]!!
            )
        )
    }
    fun getGun(): Gun {
        return Gun(this)
    }
}

sealed class ReloadType {
    object EntireMag : ReloadType()
    object SingleShot: ReloadType()
}