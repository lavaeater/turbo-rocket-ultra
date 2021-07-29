package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.TowerBuildingUiComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerControlComponent
import factories.tower
import ktx.ashley.allOf
import ktx.ashley.remove
import ktx.graphics.use
import ktx.math.plus
import ktx.math.vec2
import physics.getComponent

class RenderUserInterfaceSystem(private val batch: Batch) :
    IteratingSystem(
        allOf(
            TowerBuildingUiComponent::class
        ).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent<TransformComponent>()
        val towerBuildingUiComponent = entity.getComponent<TowerBuildingUiComponent>()
        towerBuildingUiComponent.ui.position.set(transform.position)
        batch.use {
            towerBuildingUiComponent.ui.render(batch, deltaTime, .1f)
        }
        if(towerBuildingUiComponent.select) {
            towerBuildingUiComponent.select = false
            val at = vec2(transform.position.x, transform.position.y) + entity.getComponent<PlayerControlComponent>().aimVector
            tower(at, towerBuildingUiComponent.ui.selectedItem)
            towerBuildingUiComponent.cancel = true
        }
    }

}