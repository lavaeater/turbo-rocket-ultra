package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ecs.components.CameraFollowComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.vec2
import ktx.math.vec3
import physics.Mappers
import screens.GameScreen

class CameraUpdateSystem(
    private val camera: OrthographicCamera = inject(),
    private val viewport: ExtendViewport = inject()
) :

    IteratingSystem(
        allOf(
            CameraFollowComponent::class,
            TransformComponent::class
        ).get(), 3
    ) {

    private val transformComponents = mutableSetOf<TransformComponent>()
    private val cameraPosition = vec2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        transformComponents.add(Mappers.transformMapper.get(entity))

        cameraPosition.set(transformComponents.map { it.position.x }.sum() /transformComponents.count().toFloat(), transformComponents.map { it.position.y }.sum() /transformComponents.count().toFloat() )

        camera.position.lerp(
            vec3(cameraPosition, 0f), 0.5f)

        viewport.minWorldWidth = (transformComponents.maxOf { it.position.x } - transformComponents.minOf { it.position.x } + 15f).coerceIn(GameScreen.GAMEWIDTH, GameScreen.GAMEWIDTH * 3)
        viewport.minWorldHeight = (transformComponents.maxOf { it.position.y } - transformComponents.minOf { it.position.y } + 15f).coerceIn(GameScreen.GAMEHEIGHT, GameScreen.GAMEHEIGHT * 3)
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        camera.update()
    }
}