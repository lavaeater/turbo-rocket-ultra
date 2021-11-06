package ecs.components.pickups

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import features.pickups.ILoot
import features.pickups.ILootTable
import features.pickups.LootTable

class LootComponent : Component, Pool.Poolable {
    val lootTable: ILootTable = LootTable(mutableListOf(),1)
    var activated = false
    override fun reset() {
        activated = false
    }
}