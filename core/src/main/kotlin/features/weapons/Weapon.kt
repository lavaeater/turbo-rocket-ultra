package features.weapons

class Weapon(val weaponDefinition: WeaponDefinition) {
    val soundRadius = weaponDefinition.soundRadius
    val name = weaponDefinition.name
    val weaponType = weaponDefinition.weaponType
    val ammoCap = weaponDefinition.ammoCap
    var ammoRemaining = weaponDefinition.ammoCap
    val rof = weaponDefinition.rof
    val accuracyOrHitArcForMelee = weaponDefinition.accuracyOrHitArcForMelee
    val numberOfProjectiles = weaponDefinition.numberOfProjectiles
    val spreadOrMeleeRangeOrArea = weaponDefinition.spreadOrMeleeRangeOrArea
    val textureName = weaponDefinition.textureName
    val ammoType = weaponDefinition.ammoType
    val damageRange = weaponDefinition.damageRange
    val reloadDelay = weaponDefinition.reloadDelay
    val reloadType = weaponDefinition.reloadType
    val audio = weaponDefinition.audio
    val rotate = weaponDefinition.rotate
    val handleKey = weaponDefinition.handleKey
    override fun toString(): String {
        return "$name: $weaponType"
    }
}