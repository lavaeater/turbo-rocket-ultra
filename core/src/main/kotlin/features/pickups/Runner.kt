package features.pickups

import features.weapons.AmmoType
import java.io.Console

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

    fun main() {
        val lootList = mutableListOf<ILoot>()
        lootList.add(AmmoLoot(AmmoType.nineMilliMeters, 6..32, 30f))
        //lootList.add(NullValue(60f))
        lootList.add(AmmoLoot(AmmoType.twelveGaugeShotgun, 4..16, 10f))

        val lootTable = LootTable(lootList, 1)

        for (i in 0 until 10) {
            for(loot in lootTable.result) {
                println(loot)
            }
        }

    }