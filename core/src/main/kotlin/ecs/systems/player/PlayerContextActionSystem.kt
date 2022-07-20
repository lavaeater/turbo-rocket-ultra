package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.ContextActionComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.contextAction
import physics.playerControl
import physics.textureRegionComponent

class PlayerContextActionSystem : IteratingSystem(allOf(ContextActionComponent::class, PlayerControlComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val contextActionComponent = entity.contextAction()
        val spriteComponent = entity.textureRegionComponent()
        spriteComponent.extraTextureRegions["contextaction"] = contextActionComponent.textureRegion
        val playerControlComponent = entity.playerControl()
        if(playerControlComponent.doContextAction) {
            contextActionComponent.contextAction()
            spriteComponent.extraTextureRegions.remove("contextaction")
            entity.remove<ContextActionComponent>()
        }
    }

}