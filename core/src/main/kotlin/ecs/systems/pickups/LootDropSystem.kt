package ecs.systems.pickups

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.pickups.LootDropComponent
import factories.lootBox
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class LootDropSystem : IteratingSystem(allOf(LootDropComponent::class).get()) {
    private val lootMapper = mapperFor<LootDropComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val lootComponent = lootMapper.get(entity)
        if(lootComponent.activated) {
            lootComponent.activated = false
            val result = lootComponent.lootTable.result
            if(result.any())
                lootBox(lootComponent.at, lootComponent.lootTable.result)
        }
    }
}