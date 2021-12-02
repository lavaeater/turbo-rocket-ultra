package features.pickups

import features.weapons.AmmoType

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