package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.TransformComponent
import eater.injection.InjectionContext.Companion.inject
import ecs.components.player.PlayerControlComponent
import ecs.systems.sectionX
import ecs.systems.sectionY
import ktx.ashley.allOf
import map.grid.GridMapManager
import eater.physics.getComponent

class PlayerHasBeenHereSystem: IteratingSystem(allOf(PlayerControlComponent::class, TransformComponent::class).get()) {
    val mapManager by lazy { inject<GridMapManager>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = entity.getComponent<TransformComponent>().position
        val tileX = position.sectionX()
        val tileY = position.sectionY()
        mapManager.visit(tileX, tileY)
    }
}