package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.ObstacleComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.CharacterSpriteComponent
import ecs.components.graphics.RenderableComponent
import ecs.components.player.PlayerComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import ktx.math.vec2
import tru.Assets
import java.util.Comparator

class RenderMiniMapSystem : SortedIteratingSystem(allOf(RenderableComponent::class, TransformComponent::class).get(), object :
    Comparator<Entity> {
    val mapper = mapperFor<RenderableComponent>()
    override fun compare(p0: Entity, p1: Entity): Int {
        return mapper.get(p0).layer.compareTo(mapper.get(p1).layer)
    }}, 20) {
    private val tMapper = mapperFor<TransformComponent>()
    private val pMapper = mapperFor<PlayerComponent>()
    private val gMapper = mapperFor<ObjectiveComponent>()
    private val oMapper = mapperFor<ObstacleComponent>()
    private val sMapper = mapperFor<CharacterSpriteComponent>()
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

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = tMapper.get(entity)
        if((gMapper.has(entity) || oMapper.has(entity) && transform.position.dst2(camera.position.x, camera.position.y) < 200000f) || (sMapper.has(entity) && transform.position.dst2(camera.position.x, camera.position.y) < 20000f)) {

            var color = Color.RED
            var radius = .1f
            if (pMapper.has(entity)) {
                color = Color.WHITE
                radius = .1f
            }
            if(gMapper.has(entity)) {
                color = Color.GREEN
                radius = .1f
            }
            center.set(
                transform.position.x / scale + xOffset, transform.position.y / scale + yOffset
            )
            shapeDrawer.filledCircle(center, radius, color)
        }
    }
}