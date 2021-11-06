package ecs.components.pickups

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import features.pickups.ILoot
import features.pickups.ILootTable
import features.pickups.LootTable
import ktx.math.vec2

class LootComponent: Component, Pool.Poolable {
    var loot: List<ILoot> = emptyList()
    override fun reset() {
        loot = emptyList()
    }
}

class LootDropComponent : Component, Pool.Poolable {
    val lootTable: ILootTable = LootTable(mutableListOf(),1)
    val at = vec2()
    var activated = false
    override fun reset() {
        activated = false
        at.setZero()
    }
}