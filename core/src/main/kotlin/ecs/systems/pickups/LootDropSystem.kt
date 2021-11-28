package ecs.systems.pickups

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.pickups.LootDropComponent
import factories.lootBox
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.getComponent

class LootDropSystem : IteratingSystem(allOf(LootDropComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val lootComponent = AshleyMappers.lootDrop.get(entity)
        if(lootComponent.activated) {
            lootComponent.activated = false
            val result = lootComponent.lootTable.result
            if(result.any())
                lootBox(lootComponent.at, result)
        }
    }
}