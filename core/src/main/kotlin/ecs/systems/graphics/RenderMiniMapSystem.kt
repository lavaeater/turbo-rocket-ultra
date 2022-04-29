package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.MiniMapComponent
import ecs.components.graphics.Shape
import ecs.components.graphics.TextureComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.vec2
import map.grid.GridMapManager
import physics.getComponent
import tru.Assets

@OptIn(ExperimentalStdlibApi::class)
class RenderMiniMapSystem : SortedIteratingSystem(allOf(TextureComponent::class, TransformComponent::class, MiniMapComponent::class).get(),
    Comparator<Entity> { p0, p1 -> p1.getComponent<TextureComponent>().layer.compareTo(p0.getComponent<TextureComponent>().layer) }, 32) {
    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val scale = 1/200f
    private val center = vec2()
    private val camera by lazy { inject<OrthographicCamera>() }
    private val xOffset get() = camera.position.x + camera.viewportWidth / 3
    private val yOffset get() = camera.position.y + camera.viewportHeight / 3

    private val mapManager by lazy { inject<GridMapManager>() }

    override fun update(deltaTime: Float) {
        shapeDrawer.batch.use {
            //Here, first, draw the entire visited map as squares of suitable size.
            mapManager.renderMiniMap(shapeDrawer, xOffset, yOffset, scale)
            super.update(deltaTime)
        }
    }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent<TransformComponent>()
        val miniMapComponent = entity.getComponent<MiniMapComponent>()
        if(transform.position.dst2(camera.position.x, camera.position.y) < 2000f) {
            center.set(
                transform.position.x * scale + xOffset, transform.position.y * scale + yOffset
            )
            when(miniMapComponent.miniMapShape) {
                Shape.Rectangle -> shapeDrawer.filledRectangle((transform.position.x - 2f) * scale + xOffset, (transform.position.y - 3f) * scale + yOffset, 2f * (scale * 10), 3f * (scale * 10), miniMapComponent.color)
                Shape.Dot -> shapeDrawer.filledCircle(center, .1f, miniMapComponent.color)
            }
        }
    }
}