package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.DestroyComponent
import ecs.components.enemy.EnemyComponent
import ktx.ashley.allOf
import physics.getComponent

class EnemyDeathSystem : IteratingSystem(allOf(EnemyComponent::class).get()) {

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.getComponent<EnemyComponent>().health < 0) {
            entity.add(DestroyComponent())
        }
    }
}