package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.enemy.AttackableProperties
import ecs.components.player.*
import ecs.systems.graphics.GameConstants
import ktx.ashley.allOf
import eater.physics.getComponent
import eater.physics.has


class PlayerDeathSystem : IteratingSystem(allOf(PlayerComponent::class, AttackableProperties::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pc = entity.getComponent<PlayerComponent>()
        val ap = entity.getComponent<AttackableProperties>()


        if (ap.isDead && pc.player.lives > 0 && !entity.has<PlayerWaitsForRespawn>() && !entity.has<PlayerIsRespawning>()) {
            pc.player.lives -= 1
            entity.add(engine.createComponent(PlayerWaitsForRespawn::class.java))
        } else if (ap.isDead && pc.player.lives <= 0) {
            entity.add(engine.createComponent(PlayerIsDead::class.java))
        }

        if (entity.has<PlayerIsDead>() || entity.has<PlayerWaitsForRespawn>()) {
            if (PlayerControlComponent.has(entity)) {
                val controlComponent = PlayerControlComponent.get(entity)
                controlComponent.waitsForRespawn = true
            }
        }

        if (entity.has<PlayerWaitsForRespawn>()) {
            val dc = entity.getComponent<PlayerWaitsForRespawn>()
            dc.coolDown -= deltaTime
            if (dc.coolDown < 0f) {
                entity.remove(PlayerWaitsForRespawn::class.java)
                entity.add(engine.createComponent(PlayerIsRespawning::class.java))
                ap.health = GameConstants.ENEMY_BASE_HEALTH
            }
        }
        if (entity.has<PlayerIsRespawning>()) {
            val rc = entity.getComponent<PlayerIsRespawning>()
            rc.coolDown -= deltaTime
            if (rc.coolDown < 0f) {
                entity.remove(PlayerIsRespawning::class.java)
                entity.getComponent<PlayerControlComponent>().waitsForRespawn = false
            }
        }

    }
}