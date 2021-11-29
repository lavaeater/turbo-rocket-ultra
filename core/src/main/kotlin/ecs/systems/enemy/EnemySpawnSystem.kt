package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySpawnerComponent
import ecs.components.gameplay.TransformComponent
import factories.enemy
import factories.spawner
import ktx.ashley.allOf
import ktx.math.random
import ktx.math.vec2
import physics.getComponent
import screens.CounterObject

class EnemySpawnSystem : IteratingSystem(allOf(EnemySpawnerComponent::class, TransformComponent::class).get()) {
    val enemyCount get() = engine.getEntitiesFor(allOf(EnemyComponent::class).get()).count()
    val spawnPosition = vec2()

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val spawnerComponent = entity.getComponent<EnemySpawnerComponent>()
        spawnerComponent.coolDown -= deltaTime
        if(spawnerComponent.coolDown <= 0f && enemyCount < CounterObject.numberOfEnemies && CounterObject.maxSpawnedEnemies > 0) {
            spawnerComponent.reset()
            spawnPosition.set(entity.getComponent<TransformComponent>().position)
            spawnPosition.set(spawnPosition.x + (-2f..2f).random(), spawnPosition.y + (-2f..2f).random())
            enemy(spawnPosition)
            CounterObject.maxSpawnedEnemies--
        }
    }
}