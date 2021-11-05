package features.weapons

class Weapon(private val weaponDefinition: WeaponDefinition) {
    val name = weaponDefinition.name
    val ammoCap = weaponDefinition.ammoCap
    var ammoRemaining = weaponDefinition.ammoCap
    val rof = weaponDefinition.rof
    val accuracy = weaponDefinition.accuracy
    val numberOfProjectiles = weaponDefinition.numberOfProjectiles
    val maxSpread = weaponDefinition.maxSpread
    val textureName = weaponDefinition.textureName
}