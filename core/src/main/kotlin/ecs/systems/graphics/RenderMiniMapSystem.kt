package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.ObjectiveComponent
import ecs.components.gameplay.ObstacleComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.*
import ecs.components.graphics.renderables.RenderableType
import ecs.components.player.PlayerComponent
import factories.enemy
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import ktx.math.vec2
import tru.Assets
import java.util.Comparator

class RenderMiniMapSystem : SortedIteratingSystem(allOf(RenderLayerComponent::class, TransformComponent::class).get(), object :
    Comparator<Entity> {
    val mapper = mapperFor<RenderLayerComponent>()
    override fun compare(p0: Entity, p1: Entity): Int {
        return mapper.get(p1).layer.compareTo(mapper.get(p0).layer)
    }}, 20) {
    private val transformMapper = mapperFor<TransformComponent>()
    private val playerMapper = mapperFor<PlayerComponent>()
    private val objectiveMapper = mapperFor<ObjectiveComponent>()
    private val obstacleMapper = mapperFor<ObstacleComponent>()
    private val enemyMapper = mapperFor<EnemyComponent>()


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
        val transform = transformMapper.get(entity)
        if(transform.position.dst2(camera.position.x, camera.position.y) < 200000f) {
            center.set(
                transform.position.x / scale + xOffset, transform.position.y / scale + yOffset
            )
            var color = Color.RED
            var radius = .1f
            if(enemyMapper.has(entity)) {
                color = Color(.8f, 0f, 0f, 1f)
                radius = .1f
                shapeDrawer.filledCircle(center, radius, color)
            }
            if (playerMapper.has(entity)) {
                color = Color.WHITE
                radius = .1f
                shapeDrawer.filledCircle(center, radius, color)
            }
            if(objectiveMapper.has(entity)) {
                shapeDrawer.filledRectangle(
                    (transform.position.x - 2f) / scale + xOffset,
                    (transform.position.y - 3f) / scale + yOffset,
                    2f / (scale / 10),
                    3f / (scale / 10),
                    if(objectiveMapper.get(entity).touched) Color.PURPLE else Color.GREEN)
            }
            if(obstacleMapper.has(entity)) {
                shapeDrawer.filledRectangle(
                    (transform.position.x - 2f) / scale + xOffset,
                    (transform.position.y - 3f) / scale + yOffset,
                    2f / (scale / 10),
                    3f / (scale / 10),
                    Color.BLUE)
            }
        }
    }
}