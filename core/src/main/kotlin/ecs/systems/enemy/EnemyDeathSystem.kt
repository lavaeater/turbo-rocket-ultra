package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.DestroyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.pickups.LootDropComponent
import factories.gibs
import factories.lootBox
import ktx.ashley.allOf
import physics.getComponent
import physics.hasComponent

class EnemyDeathSystem : IteratingSystem(allOf(EnemyComponent::class).get()) {

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.getComponent<EnemyComponent>().health < 0) {
            val transformComponent = entity.getComponent<TransformComponent>()
            if(entity.hasComponent<LootDropComponent>()) {
                val result = entity.getComponent<LootDropComponent>().lootTable.result
                if(result.any()) {
                    lootBox(transformComponent.position, result)
                }
            }
            gibs(transformComponent.position, 0f)
            entity.add(DestroyComponent())
        }
    }
}