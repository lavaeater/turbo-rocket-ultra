package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.fx.SplatterComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.RenderLayerComponent
import ecs.components.graphics.RenderableComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.graphics.use
import tru.Assets

class RenderSystem(
    private val batch: Batch
) : SortedIteratingSystem(
    allOf(
        TransformComponent::class,
        RenderLayerComponent::class
    ).get(), object : Comparator<Entity> {
        val renderableMapper = mapperFor<RenderLayerComponent>()
        val transformMapper = mapperFor<TransformComponent>()

        override fun compare(p0: Entity, p1: Entity): Int {
            val layer0 = renderableMapper.get(p0).layer
            val layer1 = renderableMapper.get(p1).layer
            return if (layer0 == layer1) {
                val y0 = (transformMapper.get(p0).position.y)
                val y1 = (transformMapper.get(p1).position.y)
                val compareVal = y0.compareTo(y1)
                if (compareVal != 0)
                    return compareVal
                else {
                    val x0 = transformMapper.get(p0).position.y
                    val x1 = transformMapper.get(p1).position.y
                    x1.compareTo(x0)
                }
            } else {
                layer0.compareTo(layer1)
            }
        }
    }, 1
) {

    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val pixelsPerMeter = 16f
    private val scale = 1 / pixelsPerMeter
    private var animationStateTime = 0f

    private val transformMapper = mapperFor<TransformComponent>()
    private val renderableMapper = mapperFor<RenderableComponent>()
    private val splatterMapper = mapperFor<SplatterComponent>()

    override fun update(deltaTime: Float) {
        animationStateTime += deltaTime
        forceSort()
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //1. Just render the texture without animation
        val transform = transformMapper.get(entity)

        entity[renderableMapper]?.renderable?.render(
            transform.position,
            transform.rotation,
            scale,
            animationStateTime,
            batch,
            shapeDrawer
        )
        entity[splatterMapper]?.render(
            transform.position,
            transform.rotation,
            scale,
            animationStateTime,
            batch,
            shapeDrawer
        )
    }
}


