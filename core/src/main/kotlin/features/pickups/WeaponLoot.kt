package features.pickups

import features.weapons.WeaponDefinition

class WeaponLoot(
    val weaponDefinition: WeaponDefinition,
    probability: Float
) : Loot(probability) {
    override fun toString(): String {
        return weaponDefinition.name
    }
}