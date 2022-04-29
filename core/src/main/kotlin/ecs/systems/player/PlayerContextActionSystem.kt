package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.TextureComponent
import ecs.components.player.ContextActionComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.getComponent

class PlayerContextActionSystem : IteratingSystem(allOf(ContextActionComponent::class, PlayerControlComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val contextActionComponent = entity.getComponent<ContextActionComponent>()
        val textureComponent = entity.getComponent<TextureComponent>()
        textureComponent.extraTextures["contextaction"] = Pair(contextActionComponent.texture, 1.0f)
        val playerControlComponent = entity.getComponent<PlayerControlComponent>()
        if(playerControlComponent.doContextAction) {
            contextActionComponent.contextAction()
            textureComponent.extraTextures.remove("contextaction")
            entity.remove<ContextActionComponent>()
        }
    }

}