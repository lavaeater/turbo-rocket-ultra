package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.*
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import ktx.math.vec2
import physics.getComponent
import tru.Assets

class RenderMiniMapSystem : SortedIteratingSystem(allOf(TextureComponent::class, TransformComponent::class, MiniMapComponent::class).get(), object :
    Comparator<Entity> {
    val mapper = mapperFor<TextureComponent>()
    override fun compare(p0: Entity, p1: Entity): Int {
        return mapper.get(p1).layer.compareTo(mapper.get(p0).layer)
    }}, 20) {
    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val scale = 100f
    private val center = vec2()
    private val camera by lazy { inject<OrthographicCamera>() }
    private val xOffset get() = camera.position.x + camera.viewportWidth / 3
    private val yOffset get() = camera.position.y + camera.viewportHeight / 3

    override fun update(deltaTime: Float) {
        shapeDrawer.batch.use {
            super.update(deltaTime)
        }
    }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.getComponent<TransformComponent>()
        val miniMapComponent = entity.getComponent<MiniMapComponent>()
        if(transform.position.dst2(camera.position.x, camera.position.y) < 200000f) {
            center.set(
                transform.position.x / scale + xOffset, transform.position.y / scale + yOffset
            )
            when(miniMapComponent.miniMapShape) {
                Shape.Rectangle -> shapeDrawer.filledRectangle((transform.position.x - 2f) / scale + xOffset, (transform.position.y - 3f) / scale + yOffset, 2f / (scale / 10), 3f / (scale / 10), miniMapComponent.color)
                Shape.Dot -> shapeDrawer.filledCircle(center, .1f, miniMapComponent.color)
            }
        }
    }
}