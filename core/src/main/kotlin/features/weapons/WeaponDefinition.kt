package features.weapons

import com.badlogic.gdx.audio.Sound
import tru.Assets

/**
 *
 * @param ROF is in shots per minute
 */
class WeaponDefinition(
    val name: String,
    val weaponType: WeaponType,
    val ammoCap: Int,
    val rof: Float,
    val accuracyOrHitArcForMelee: Float,
    val numberOfProjectiles: Int,
    val spreadOrMeleeRangeOrArea: Float,
    val textureName: String,
    val ammoType: AmmoType,
    val damageRange: ClosedFloatingPointRange<Float>,
    val reloadDelay: Float,
    val reloadType: ReloadType,
    val audio: Map<String, Sound>,
    val soundRadius: Float,
    val rotate: Boolean = false,
    val handleKey: String = "",
) {
    companion object {
        val molotov = WeaponDefinition(
            "Molotov Cocktail",
            WeaponType.ThrownArea,
            1,
            30f,
            15f, //
            1,
            15f,
            GunFrames.handGun,
            AmmoType.Molotov,
            5f..15f,
            0f,
            ReloadType.SingleShot,
            Assets.gunAudio["glock17"]!!,
            0f,
            handleKey = "melee"
        )
        val baseballBat = WeaponDefinition(
            "Baseball Bat",
            WeaponType.Melee,
            -1,
            60f,
            45f, //
            -1,
            5f,
            GunFrames.bat,
            AmmoType.MeleeWeapon,
            5f..15f,
            0f,
            ReloadType.MeleeWeapon,
            Assets.gunAudio["glock17"]!!,//TODO exhange for bat or something
            0f,
            true,
            "melee"
        )
        val weapons = listOf(
            baseballBat,
            molotov,
            WeaponDefinition(
                "Glock 17",
                WeaponType.Projectile,
                17,
                120f,
                3f,
                1,
                .125f,
                GunFrames.handGun,
                AmmoType.NineMilliMeters,
                8f..16f,
                2f,
                ReloadType.EntireMag,
                Assets.gunAudio["glock17"]!!,
                50f
            ),
            WeaponDefinition(
                "Franchi Spas 12",
                WeaponType.Projectile,
                8,
                60f,
                5f,
                16,
                15f,
                GunFrames.spas12,
                AmmoType.TwelveGaugeShotgun,
                12f..24f,
                1f,
                ReloadType.SingleShot,
                Assets.gunAudio["spas12"]!!,
                150f,
                true,
                "rifle"

            ),
            WeaponDefinition(
                "FN P90",
                WeaponType.Projectile,
                50,
                900f,
                1f,
                1,
                .125f,
                GunFrames.spas12,
                AmmoType.FnP90Ammo,
                6f..14f,
                2f,
                ReloadType.EntireMag,
                Assets.gunAudio["fnp90"]!!,
                100f
            )
        )
    }

    fun getWeapon(): Weapon {
        return Weapon(this)
    }
}

