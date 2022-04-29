package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.MiniMapComponent
import ecs.components.graphics.RenderableComponent
import ecs.components.graphics.Shape
import ecs.components.graphics.SpriteComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.vec2
import map.grid.GridMapManager
import physics.AshleyMappers
import physics.renderable
import physics.sprite
import tru.Assets

class RenderMiniMapSystem(priority: Int): SortedIteratingSystem(allOf(RenderableComponent::class, TransformComponent::class, MiniMapComponent::class).get(),
    Comparator<Entity> { p0, p1 -> p1.renderable().layer.compareTo(p0.renderable().layer) }, priority) {
    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val scale = 1/50f
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

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = AshleyMappers.transform.get(entity)
        val miniMapComponent = AshleyMappers.miniMap.get(entity)
        if(transform.position.dst2(camera.position.x, camera.position.y) < 20000f) {
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