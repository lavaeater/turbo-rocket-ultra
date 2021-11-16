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
) : Loot(probability)

    fun main() {
        val lootList = mutableListOf<ILoot>()
        lootList.add(AmmoLoot(AmmoType.NineMilliMeters, 6..32, 30f))
        //lootList.add(NullValue(60f))
        lootList.add(AmmoLoot(AmmoType.TwelveGaugeShotgun, 4..16, 10f))

        val lootTable = LootTable(lootList, 1)

        for (i in 0 until 10) {
            for(loot in lootTable.result) {
                println(loot)
            }
        }

    }