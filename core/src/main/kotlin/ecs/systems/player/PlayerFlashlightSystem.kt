package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.TransformComponent
import ecs.components.player.FlashlightComponent
import ecs.components.player.PlayerControlComponent
import ktx.ashley.allOf
import physics.getComponent

/**
 * We need a cool-down system, which determines the rate of fire.
 *
 * This means you can always shoot if the weapon is cool.
 */

class PlayerFlashlightSystem : IteratingSystem(
    allOf(
        FlashlightComponent::class,
        TransformComponent::class,
        PlayerControlComponent::class
    ).get(), 100
) {
    @OptIn(ExperimentalStdlibApi::class)
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