package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import data.Players
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.InFrustumComponent
import ecs.components.graphics.InLineOfSightComponent
import ecs.components.player.PlayerControlComponent
import input.canISeeYouFromHere
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.addComponent
import physics.getComponent

/**
 * Checks if entity is within 270 degrees of any players field of view. If so, it shall be rendered
 */
class LineOfSightCullingSystem : IteratingSystem(allOf(InFrustumComponent::class, TransformComponent::class).get()) {
    @OptIn(ExperimentalStdlibApi::class)
    private val players by lazy { Players.players.values.map { it.entity }}

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        //Every entity is an enemy
        val position = entity.getComponent<TransformComponent>().position
        if(players.any {
                canISeeYouFromHere(
                    it.getComponent<TransformComponent>().position,
                    it.getComponent<PlayerControlComponent>().aimVector,
                    position,
                    270f
                )
        }) {
            entity.addComponent<InLineOfSightComponent>()
        } else {
            entity.remove<InLineOfSightComponent>()
        }
    }
}