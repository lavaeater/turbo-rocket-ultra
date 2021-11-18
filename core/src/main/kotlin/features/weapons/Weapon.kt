package features.weapons

class Weapon(weaponDefinition: WeaponDefinition) {
    val name = weaponDefinition.name
    val weaponType = weaponDefinition.weaponType
    val ammoCap = weaponDefinition.ammoCap
    var ammoRemaining = weaponDefinition.ammoCap
    val rof = weaponDefinition.rof
    val accuracyOrHitArcForMelee = weaponDefinition.accuracyOrHitArcForMelee
    val numberOfProjectiles = weaponDefinition.numberOfProjectiles
    val spreadOrMeleeRange = weaponDefinition.spreadOrMeleeRange
    val textureName = weaponDefinition.textureName
    val ammoType = weaponDefinition.ammoType
    val damageRange = weaponDefinition.damageRange
    val reloadDelay = weaponDefinition.reloadDelay
    val reloadType = weaponDefinition.reloadType
    val audio = weaponDefinition.audio
}