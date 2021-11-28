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
import physics.has

class EnemyDeathSystem : IteratingSystem(allOf(EnemyComponent::class).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = entity.getComponent<EnemyComponent>()
        if (enemyComponent.isDead) {
            val transformComponent = entity.getComponent<TransformComponent>()
            if (entity.has<LootDropComponent>()) {
                val result = entity.getComponent<LootDropComponent>().lootTable.result
                if (result.any()) {
                    lootBox(transformComponent.position, result)
                }
            }
            enemyComponent.lastHitBy.kills++

            gibs(transformComponent.position, enemyComponent.lastShotAngle)
            entity.add(DestroyComponent())
        }
    }
}