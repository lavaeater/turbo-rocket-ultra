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
    val damageRange: IntRange,
    val reloadDelay: Float,
    val reloadType: ReloadType,
    val audio: Map<String, Sound>
) {
    companion object {
        val molotov = WeaponDefinition(
            "Molotov Cocktail",
            WeaponType.ThrownArea,
            1000,
            30f,
            15f, //
            1,
            15f,
            GunFrames.handGun,
            AmmoType.Molotov,
            5..15,
            0f,
            ReloadType.MeleeWeapon,
            Assets.gunAudio["glock17"]!!
        )
        val weapons = listOf(
            WeaponDefinition(
                "Baseball Bat",
                WeaponType.Melee,
                -1,
                60f,
                45f, //
                -1,
                5f,
                GunFrames.handGun, //TODO exhange for bat or something
                AmmoType.MeleeWeapon,
                5..15,
                0f,
                ReloadType.MeleeWeapon,
                Assets.gunAudio["glock17"]!!//TODO exhange for bat or something
            ),
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
                8..16,
                2f,
                ReloadType.EntireMag,
                Assets.gunAudio["glock17"]!!
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
                12..24,
                1f,
                ReloadType.SingleShot,
                Assets.gunAudio["spas12"]!!
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
                6..14,
                2f,
                ReloadType.EntireMag,
                Assets.gunAudio["fnp90"]!!
            )
        )
    }

    fun getWeapon(): Weapon {
        return Weapon(this)
    }
}

