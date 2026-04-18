package components.pickups

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import gamePlay.pickups.ILoot
import gamePlay.pickups.LootTable

class LootComponent: Component, Pool.Poolable {
    var loot: List<ILoot> = emptyList()
    var lootTable: LootTable? = null
    override fun reset() {
        loot = emptyList()
        lootTable = null
    }
}