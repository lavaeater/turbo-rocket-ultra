package features.pickups

import features.weapons.AmmoType
import features.weapons.WeaponDefinition

class AmmoLoot(
    val ammoType: AmmoType,
    val countRange: IntRange,
    probability: Float
) : Loot(probability) {
    val amount get() = countRange.random()
    override fun toString(): String {
        return "${ammoType}: ${amount}"
    }
}

class WeaponLoot(
    val weaponDefinition: WeaponDefinition,
    probability: Float
) : Loot(probability) {
    override fun toString(): String {
        return weaponDefinition.name
    }
}