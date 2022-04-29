package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf

class PlayerBuildSystem: IteratingSystem(
    allOf(PlayerControlComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {

    }
}