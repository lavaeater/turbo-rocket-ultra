package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.CameraFollowComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.vec3
import physics.Mappers

class CameraUpdateSystem(
    private val camera: OrthographicCamera = inject()) :
    IteratingSystem(allOf(
        CameraFollowComponent::class,
        TransformComponent::class).get(), 3) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        camera.position.lerp(vec3(Mappers.transformMapper.get(entity).position, 0f), 0.5f)
    }
}