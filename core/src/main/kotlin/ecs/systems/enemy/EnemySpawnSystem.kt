package ecs.systems.enemy

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.EnemyComponent
import ecs.components.enemy.EnemySpawnerComponent
import ecs.components.gameplay.TransformComponent
import factories.enemy
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.random
import ktx.math.vec2
import physics.AshleyMappers
import screens.CounterObject
import story.FactsOfTheWorld
import story.fact.Facts

class EnemySpawnSystem : IteratingSystem(allOf(EnemySpawnerComponent::class, TransformComponent::class).get()) {
    private val enemyCount get() = engine.getEntitiesFor(allOf(EnemyComponent::class).get()).count()
    private val spawnPosition = vec2()
    private val factsOfTheWorld = inject<FactsOfTheWorld>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val spawnerComponent = AshleyMappers.enemySpawner.get(entity)
        spawnerComponent.coolDown -= deltaTime
        if(spawnerComponent.coolDown <= 0f && enemyCount < CounterObject.maxEnemies && CounterObject.maxSpawnedEnemies > 0) {
            if(factsOfTheWorld.getBoolean(Facts.AcceleratingSpawns)) {
                val factor = factsOfTheWorld.getFloatValue(Facts.AcceleratingSpawnsFactor)
                spawnerComponent.coolDownRange = (spawnerComponent.coolDownRange.start / factor)..(spawnerComponent.coolDownRange.endInclusive / factor)
            }
            spawnerComponent.reset()
            for(i in 0 until spawnerComponent.waveSize) {
                spawnPosition.set(AshleyMappers.transform.get(entity).position)
                spawnPosition.set(spawnPosition.x + (-2f..2f).random(), spawnPosition.y + (-2f..2f).random())
                enemy(spawnPosition)
                CounterObject.maxSpawnedEnemies--
            }
        }
    }
}