package systems.enemy

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import components.AgentProperties
import physics.addComponent
import physics.getComponent
import turbofacts.Factoids
import turbofacts.TurboFactsOfTheWorld
import turbofacts.addToIntStat
import components.AudioChannels
import components.enemy.AttackableProperties
import components.gameplay.DestroyComponent
import components.player.PlayerControlComponent
import factories.gibs
import factories.lootBox
import ktx.ashley.allOf
import physics.*
import tru.Assets

object FitnessTracker {
    val fitnessData = mutableListOf<FitnessData>()
    fun saveFitnessDataFor(enemy: Entity) {
        val key = enemy.agentProps().id
        var fd = fitnessData.firstOrNull { it.enemyId == key }
        if (fd == null) {
            val bt = enemy.behavior().tree
            bt.`object` = null
            fitnessData.add(FitnessData(key, enemy.fitnesScore(), byteArrayOf()))
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
) : IteratingSystem(allOf(AgentProperties::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = AshleyMappers.agentProps.get(entity)
        val attackableProperties = entity.getComponent<AttackableProperties>()
        if (attackableProperties.isDead) {
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
            if(PlayerControlComponent.has(attackableProperties.lastHitBy)) {
                val player = PlayerControlComponent.get(attackableProperties.lastHitBy).player
                player.kills++
                addToIntStat(1, "Player", player.playerId, "KillCount")
                addToIntStat(1, Factoids.EnemyKillCount)
            }

            gibs(transformComponent.position, enemyComponent.lastShotAngle)
            entity.addComponent<DestroyComponent>()
        }
    }
}
