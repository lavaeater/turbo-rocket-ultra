package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.BoxComponent
import ecs.components.graphics.CharacterSpriteComponent
import ecs.components.graphics.RenderLayerComponent
import ecs.components.player.PlayerComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import ktx.math.vec2
import physics.getComponent
import physics.hasComponent
import tru.Assets
import java.util.Comparator

class RenderMiniMapSystem : SortedIteratingSystem(allOf(RenderLayerComponent::class, TransformComponent::class).get(), object :
    Comparator<Entity> {
    val mapper = mapperFor<RenderLayerComponent>()
    override fun compare(p0: Entity, p1: Entity): Int {
        return mapper.get(p1).layer.compareTo(mapper.get(p0).layer)
    }}, 20) {
    private val tMapper = mapperFor<TransformComponent>()
    private val pMapper = mapperFor<PlayerComponent>()
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

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = tMapper.get(entity)
        if(transform.position.dst2(camera.position.x, camera.position.y) < 200000f) {
            center.set(
                transform.position.x / scale + xOffset, transform.position.y / scale + yOffset
            )
            var color = Color.RED
            var radius = .1f
            if(sMapper.has(entity)) {
                color = Color(.8f, 0f, 0f, 1f)
                radius = .1f
                shapeDrawer.filledCircle(center, radius, color)
            }
            if (pMapper.has(entity)) {
                color = Color.WHITE
                radius = .1f
                shapeDrawer.filledCircle(center, radius, color)
            }
            if(entity.hasComponent<BoxComponent>()) {
                val box = entity.getComponent<BoxComponent>()
                shapeDrawer.filledRectangle(
                    (transform.position.x - box.width) / scale + xOffset,
                    (transform.position.y - box.height) / scale + yOffset,
                    box.width / (scale / 10),
                    box.height / (scale / 10),
                    box.color)
            }



        }
    }
}