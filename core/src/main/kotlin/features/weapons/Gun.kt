package features.weapons

class Gun(private val gunDefinition: GunDefinition) {
    val name = gunDefinition.name
    val ammoCap = gunDefinition.ammoCap
    var ammoRemaining = gunDefinition.ammoCap
    val rof = gunDefinition.rof
    val accuracy = gunDefinition.accuracy
    val numberOfProjectiles = gunDefinition.numberOfProjectiles
    val maxSpread = gunDefinition.maxSpread
    val textureName = gunDefinition.textureName
    val ammoType = gunDefinition.ammoType
}