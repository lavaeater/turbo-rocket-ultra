package ecs.components.pickups

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import features.pickups.ILoot

class LootComponent: Component, Pool.Poolable {
    var loot: List<ILoot> = emptyList()
    override fun reset() {
        loot = emptyList()
    }
}