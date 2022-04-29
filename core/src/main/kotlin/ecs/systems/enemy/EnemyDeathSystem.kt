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
import physics.AshleyMappers
import physics.addComponent
import physics.getComponent
import physics.has

class EnemyDeathSystem : IteratingSystem(allOf(EnemyComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = AshleyMappers.enemy.get(entity)
        if (enemyComponent.isDead) {
            val transformComponent = AshleyMappers.transform.get(entity)
            if (AshleyMappers.lootDrop.has(entity)) {
                val result = AshleyMappers.lootDrop.get(entity).lootTable.result
                if (result.any()) {
                    lootBox(transformComponent.position, result)
                }
            }
            enemyComponent.lastHitBy.kills++

            gibs(transformComponent.position, enemyComponent.lastShotAngle)
            entity.addComponent<DestroyComponent>()
        }
    }
}