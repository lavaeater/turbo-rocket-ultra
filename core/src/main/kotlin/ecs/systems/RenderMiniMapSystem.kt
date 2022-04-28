package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.CharacterSpriteComponent
import ecs.components.PlayerComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import ktx.math.vec2
import tru.Assets

class RenderMiniMapSystem : IteratingSystem(allOf(CharacterSpriteComponent::class, TransformComponent::class).get(), 20) {
    private val tMapper = mapperFor<TransformComponent>()
    private val pMapper = mapperFor<PlayerComponent>()
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
        if(transform.position.dst2(camera.position.x, camera.position.y) < 20000f) {

            var color = Color.RED
            var radius = .1f
            if (pMapper.has(entity)) {
                color = Color.GREEN
                radius = .3f
            }
            center.set(
                transform.position.x / scale + xOffset, transform.position.y / scale + yOffset
            )
            shapeDrawer.filledCircle(center, radius, color)
        }
    }
}