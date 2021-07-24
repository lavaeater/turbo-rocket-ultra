package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.UiComponent
import ecs.components.gameplay.TransformComponent
import isometric.toIsometric
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.vec2
import physics.getComponent
import ui.new.BoundTextElement
import ui.new.Carousel

class RenderUserInterfaceSystem(private val batch: Batch) :
    IteratingSystem(
        allOf(
            UiComponent::class
        ).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent<TransformComponent>()
        val userinterface = entity.getComponent<UiComponent>()
        userinterface.ui.position.set(transform.position)
        batch.use {
            userinterface.ui.render(batch, deltaTime, .1f)
        }
    }

}