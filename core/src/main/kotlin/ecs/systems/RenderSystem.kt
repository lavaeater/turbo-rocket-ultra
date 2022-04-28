package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.*
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.graphics.use
import tru.Assets
import java.util.*

class RenderSystem(
    private val batch: Batch
) : SortedIteratingSystem(
    allOf(
        TransformComponent::class,
        RenderableComponent::class
    ).get(), object : Comparator<Entity> {
        val mapper = mapperFor<RenderableComponent>()
        override fun compare(p0: Entity, p1: Entity): Int {
            return mapper.get(p0).layer.compareTo(mapper.get(p1).layer)
        }}) {

    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val pixelsPerMeter = 16f
    private val scale = 1 / pixelsPerMeter
    private var animationStateTime = 0f

    private val tMapper = mapperFor<TransformComponent>()
    private val sMapper = mapperFor<CharacterSpriteComponent>()
    private val pMapper = mapperFor<SplatterComponent>()
    private val bMapper = mapperFor<BoxComponent>()

    override fun update(deltaTime: Float) {
        animationStateTime += deltaTime
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //1. Just render the texture without animation
        val transform = tMapper.get(entity)

        entity[sMapper]?.render(
            transform.position,
            transform.rotation,
            scale,
            animationStateTime,
            batch,
            shapeDrawer)

        entity[pMapper]?.render(
            transform.position,
            transform.rotation,
            scale,
            animationStateTime,
            batch,
            shapeDrawer)
        entity[bMapper]?.render(
            transform.position,
            transform.rotation,
            scale,
            animationStateTime,
            batch,
            shapeDrawer)
    }
}


