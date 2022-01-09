package ecs.systems.enemy

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.AudioChannels
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.DestroyComponent
import factories.gibs
import factories.lootBox
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.addComponent
import story.FactsOfTheWorld
import story.fact.Facts
import tru.Assets

class EnemyDeathSystem(
    private val audioPlayer: AudioPlayer,
    private val factsOfTheWorld: FactsOfTheWorld) : IteratingSystem(allOf(EnemyComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = AshleyMappers.enemy.get(entity)
        if (enemyComponent.isDead) {
            audioPlayer.playNextIfEmpty(
                AudioChannels.enemyDeath,
                Assets.newSoundEffects["misc"]!!["flesh"]!!.last(),
                Assets.newSoundEffects["misc"]!!["flesh"]!!.first())
            val transformComponent = AshleyMappers.transform.get(entity)
            if (AshleyMappers.lootDrop.has(entity)) {
                val result = AshleyMappers.lootDrop.get(entity).lootTable.result
                if (result.any()) {
                    lootBox(transformComponent.position, result)
                }
            }
            enemyComponent.lastHitBy.kills++
            factsOfTheWorld.addToIntFact(Facts.EnemyKillCount, 1)

            gibs(transformComponent.position, enemyComponent.lastShotAngle)
            entity.addComponent<DestroyComponent>()
        }
    }
}