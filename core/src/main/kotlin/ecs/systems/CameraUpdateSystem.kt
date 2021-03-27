package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.CameraFollowComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.vec2
import ktx.math.vec3
import physics.Mappers

class CameraUpdateSystem(
    private val camera: OrthographicCamera = inject()
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
    }
}