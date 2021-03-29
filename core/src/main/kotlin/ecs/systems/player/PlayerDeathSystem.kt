package ecs.systems.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.ai.CoolDownComponent
import ecs.components.graphics.RenderableComponent
import ecs.components.player.PlayerComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.ashley.remove
import physics.getComponent
import physics.hasComponent

class PlayerWaitingForRespawn:CoolDownComponent() {
    init {
        coolDownRange = (10f..10f)
        reset()
    }
}

class PlayerRespawning: CoolDownComponent() {
    init {
        coolDownRange = (5f..5f)
        reset()
    }
}
class PlayerIsDead: Component

class PlayerDeathSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    val mapper = mapperFor<PlayerComponent>()
    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pc = mapper[entity]
        if(pc.player.isDead && pc.player.lives > 0 && !entity.hasComponent<PlayerWaitingForRespawn>() && !entity.hasComponent<PlayerRespawning>()) {
            pc.player.lives -= 1
            entity.add(engine.createComponent(PlayerWaitingForRespawn::class.java))
        } else if(pc.player.isDead && pc.player.lives <= 0){
            entity.add(engine.createComponent(PlayerIsDead::class.java))
        }

        if(entity.hasComponent<PlayerWaitingForRespawn>()) {
            entity.remove<RenderableComponent>()
            val dc = entity.getComponent<PlayerWaitingForRespawn>()
            dc.coolDown-= deltaTime
            if(dc.coolDown < 0f) {
                entity.remove(PlayerWaitingForRespawn::class.java)
                entity.add(engine.createComponent(PlayerRespawning::class.java))
                pc.player.health = 100
            }
        }
        if(entity.hasComponent<PlayerRespawning>()) {
            if(!entity.hasComponent<RenderableComponent>()) {
                entity.add(engine.createComponent(RenderableComponent::class.java).apply {
                    layer = 1
                })
            }
            val rc = entity.getComponent<PlayerRespawning>()
            rc.coolDown-= deltaTime
            if(rc.coolDown < 0f) {
                entity.remove(PlayerRespawning::class.java)
            }
        }
    }
}