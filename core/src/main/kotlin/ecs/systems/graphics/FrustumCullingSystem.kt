package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import data.Players
import ecs.components.graphics.InLineOfSightComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.InFrustumComponent
import ecs.components.graphics.TextureComponent
import ecs.components.player.PlayerControlComponent
import injection.Context
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
                canISeeYouFromHere(it.getComponent<TransformComponent>().position, it.getComponent<PlayerControlComponent>().aimVector, position, 270f)
        }) {
            entity.addComponent<InLineOfSightComponent>()
        } else {
            entity.remove<InLineOfSightComponent>()
        }
    }
}

/**
 * Cool thought, if this is slow:
 * Two systems. One checks only entities that we know have visiblecomponent, the other
 * checks entities with notvisiblecomponent
 */
class FrustumCullingSystem : IteratingSystem(allOf(TransformComponent::class, TextureComponent::class).get()) {
    private val camera by lazy { Context.inject<OrthographicCamera>() }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = entity.getComponent<TransformComponent>().position
        if (camera.frustum.pointInFrustum(position.x, position.y, 0f)) {
            entity.addComponent<InFrustumComponent> { }
        } else {
            entity.remove<InFrustumComponent>()
        }
    }

}