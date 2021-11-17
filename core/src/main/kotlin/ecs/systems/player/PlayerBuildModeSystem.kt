package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.TowerBuildingUiComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.PlayerMode
import input.NoOpUserInterfaceControl
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.addComponent
import physics.getComponent
import physics.has

class PlayerBuildModeSystem: IteratingSystem(
    allOf(PlayerControlComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val playerControlComponent = entity.getComponent<PlayerControlComponent>()
        if(playerControlComponent.playerMode == PlayerMode.Building && !entity.has<TowerBuildingUiComponent>()) {
            entity.getComponent<PlayerControlComponent>().controlMapper.uiControl = entity.addComponent<TowerBuildingUiComponent> {  }
        }
        if(playerControlComponent.playerMode == PlayerMode.Building && entity.has<TowerBuildingUiComponent>()) {
            if(entity.getComponent<TowerBuildingUiComponent>().cancel) {
                entity.remove<TowerBuildingUiComponent>()
                playerControlComponent.controlMapper.uiControl = NoOpUserInterfaceControl.control
                playerControlComponent.playerMode = PlayerMode.Control
            }
        }
        if(playerControlComponent.playerMode == PlayerMode.Control && entity.has<TowerBuildingUiComponent>()) {
            entity.remove<TowerBuildingUiComponent>()
            playerControlComponent.controlMapper.uiControl = NoOpUserInterfaceControl.control
        }
    }
}