package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySpawnerComponent
import ecs.components.gameplay.TransformComponent
import factories.enemy
import ktx.ashley.allOf
import physics.getComponent
import screens.GameScreen

class EnemySpawnSystem : IteratingSystem(allOf(EnemySpawnerComponent::class, TransformComponent::class).get()) {
    val enemyCount get() = engine.getEntitiesFor(allOf(EnemyComponent::class).get()).count()

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val spawnerComponent = entity.getComponent<EnemySpawnerComponent>()
        spawnerComponent.coolDown -= deltaTime
        if(spawnerComponent.coolDown <= 0f && enemyCount < GameScreen.MAX_ENEMIES) {
            spawnerComponent.reset()
            enemy(entity.getComponent<TransformComponent>().position)
        }
    }
}