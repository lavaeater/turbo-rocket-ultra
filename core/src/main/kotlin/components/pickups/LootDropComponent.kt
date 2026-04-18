package components.pickups

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import gamePlay.pickups.ILootTable
import gamePlay.pickups.LootTable
import ktx.math.vec2

class LootDropComponent : Component, Pool.Poolable {
    val lootTable: ILootTable = LootTable(mutableListOf(),1)
    val at = vec2()
    var activated = false
    override fun reset() {
        activated = false
        at.setZero()
    }
}