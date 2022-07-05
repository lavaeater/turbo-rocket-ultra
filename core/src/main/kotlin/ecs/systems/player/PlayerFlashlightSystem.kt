package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.components.TransformComponent
import ecs.components.player.FlashlightComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.getComponent

class PlayerFlashlightSystem : IteratingSystem(
    allOf(
        FlashlightComponent::class,
        TransformComponent::class,
        PlayerControlComponent::class
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val flashlight = entity.getComponent<FlashlightComponent>().flashLight
        val playerPosition = entity.getComponent<TransformComponent>().position
        val aimVector = entity.getComponent<PlayerControlComponent>().aimVector
        if(!aimVector.x.isNaN()) {
            flashlight.setPosition(playerPosition.x + aimVector.x, playerPosition.y + aimVector.y)
            flashlight.direction = aimVector.angleDeg()
        }
    }
}