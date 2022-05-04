package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.*
import ecs.components.enemy.AgentProperties
import ecs.components.player.*
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.getComponent
import physics.has


class PlayerDeathSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pc = entity.getComponent<PlayerComponent>()
        if(pc.player.isDead && pc.player.lives > 0 && !entity.has<PlayerWaitsForRespawn>() && !entity.has<PlayerIsRespawning>()) {
            pc.player.lives -= 1
            entity.add(engine.createComponent(PlayerWaitsForRespawn::class.java))
        } else if(pc.player.isDead && pc.player.lives <= 0){
            entity.add(engine.createComponent(PlayerIsDead::class.java))
        }

        if(entity.has<PlayerIsDead>() || entity.has<PlayerWaitsForRespawn>()) {
            val controlComponent = entity.getComponent<PlayerControlComponent>()
            controlComponent.waitsForRespawn = true
//            entity.getComponent<AnimatedCharacterComponent>().currentAnim =
            for(enemy in engine.getEntitiesFor(allOf(AgentProperties::class).get())) {
                if(enemy.has<IsAwareOfPlayer>() && enemy.getComponent<IsAwareOfPlayer>().player == pc.player) {
                    enemy.remove<ChasePlayer>()
                    enemy.remove<AttackPlayer>()
                    enemy.remove<IsAwareOfPlayer>()
                    enemy.remove<PlayerIsInRange>()
                    enemy.remove<NoticedSomething>()

                }
            }
        }

        if(entity.has<PlayerWaitsForRespawn>()) {
            val dc = entity.getComponent<PlayerWaitsForRespawn>()
            dc.coolDown-= deltaTime
            if(dc.coolDown < 0f) {
                entity.remove(PlayerWaitsForRespawn::class.java)
                entity.add(engine.createComponent(PlayerIsRespawning::class.java))
                pc.player.health = pc.player.startingHealth
            }
        }
        if(entity.has<PlayerIsRespawning>()) {
            val rc = entity.getComponent<PlayerIsRespawning>()
            rc.coolDown-= deltaTime
            if(rc.coolDown < 0f) {
                entity.remove(PlayerIsRespawning::class.java)
                entity.getComponent<PlayerControlComponent>().waitsForRespawn = false
            }
        }

    }
}