package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.UiComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.PlayerMode
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.addComponent
import physics.getComponent
import physics.hasComponent

class PlayerBuildModeSystem(): IteratingSystem(
    allOf(PlayerControlComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val playerControlComponent = entity.getComponent<PlayerControlComponent>()
        if(playerControlComponent.playerMode == PlayerMode.Building && !entity.hasComponent<UiComponent>()) {
            entity.addComponent<UiComponent> {  }
        }
        if(playerControlComponent.playerMode == PlayerMode.Control && entity.hasComponent<UiComponent>()) {
            entity.remove<UiComponent>()
        }
    }
}