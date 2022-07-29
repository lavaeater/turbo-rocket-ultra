package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport
import eater.ecs.components.TransformComponent
import ecs.components.graphics.CameraFollowComponent
import ecs.systems.graphics.GameConstants.GAME_HEIGHT
import ecs.systems.graphics.GameConstants.GAME_WIDTH
import ktx.ashley.allOf
import ktx.math.vec2
import ktx.math.vec3
import eater.physics.getComponent

class CameraUpdateSystem(
    private val camera: OrthographicCamera,
    private val viewport: ExtendViewport
) :
    IteratingSystem(
        allOf(
            CameraFollowComponent::class,
            TransformComponent::class
        ).get()) {

    private val transformComponents = mutableSetOf<TransformComponent>()
    private val cameraPosition = vec2()

    fun reset() {
        transformComponents.clear()
        cameraPosition.set(Vector2.Zero)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val x = transformComponents.map { it.position.x }.sum() / transformComponents.count().toFloat()
        val y = transformComponents.map { it.position.y }.sum() / transformComponents.count().toFloat()

        cameraPosition.set(
            x - y,
            (x + y) / 2
        )

        camera.position.lerp(
            vec3(cameraPosition, 0f), 0.5f
        )

        viewport.minWorldWidth =
            (transformComponents.maxOf { it.position.x } - transformComponents.minOf { it.position.x } + 30f).coerceIn(
                GAME_WIDTH,
                GAME_WIDTH * 5
            )
        viewport.minWorldHeight =
            (transformComponents.maxOf { it.position.y } - transformComponents.minOf { it.position.y } + 30f).coerceIn(
                GAME_HEIGHT,
                GAME_HEIGHT * 5
            )
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        camera.update()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        transformComponents.add(entity.getComponent())
    }
}