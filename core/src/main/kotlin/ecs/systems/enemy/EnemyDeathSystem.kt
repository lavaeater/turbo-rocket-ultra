package ecs.systems.enemy

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.AudioChannels
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.DestroyComponent
import factories.factsOfTheWorld
import factories.gibs
import factories.kryoThisBitch
import factories.lootBox
import ktx.ashley.allOf
import physics.*
import tru.Assets
import turbofacts.Factoids
import turbofacts.TurboFactsOfTheWorld

object FitnessTracker {
    val fitnessData = mutableListOf<FitnessData>()
    fun saveFitnessDataFor(enemy: Entity) {
        val key = enemy.enemy().id
        var fd = fitnessData.firstOrNull { it.enemyId == key }
        if(fd == null) {
            val bt = enemy.behavior().tree
            bt.`object` = null
            fitnessData.add(FitnessData(key, enemy.fitnesScore(), bt.kryoThisBitch()))
            bt.`object` = enemy
        } else {
            fd.fitness = enemy.fitnesScore()
        }
    }
}

data class FitnessData(val enemyId: Int, var fitness: Int, val bt: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FitnessData

        if (enemyId != other.enemyId) return false

        return true
    }

    override fun hashCode(): Int {
        return enemyId
    }
}

class EnemyDeathSystem(
    private val audioPlayer: AudioPlayer,
    private val factsOfTheWorld: TurboFactsOfTheWorld
) : IteratingSystem(allOf(EnemyComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = AshleyMappers.enemy.get(entity)
        if (enemyComponent.isDead) {
            audioPlayer.playNextIfEmpty(
                AudioChannels.enemyDeath,
                Assets.newSoundEffects["misc"]!!["flesh"]!!.last(),
                Assets.newSoundEffects["misc"]!!["flesh"]!!.first()
            )
            val transformComponent = AshleyMappers.transform.get(entity)
            if (AshleyMappers.lootDrop.has(entity)) {
                val result = AshleyMappers.lootDrop.get(entity).lootTable.result
                if (result.any()) {
                    lootBox(transformComponent.position, result)
                }
            }
            enemyComponent.lastHitBy.kills++

            addToIntStat(1, "Player", enemyComponent.lastHitBy.playerId, "KillCount")
            addToIntStat(1, Factoids.EnemyKillCount)

            gibs(transformComponent.position, enemyComponent.lastShotAngle)
            entity.addComponent<DestroyComponent>()
        }
    }
}

fun multiKey(vararg key: String): String {
    return key.joinToString(".")
}

fun stateBooleanFact(toSet: Boolean, vararg key: String): Boolean {
    return factsOfTheWorld().setBooleanFact(toSet, *key).value
}

fun addToIntStat(toAdd: Int, vararg key: String): Int {
    return factsOfTheWorld().addToInt(toAdd, *key)
}