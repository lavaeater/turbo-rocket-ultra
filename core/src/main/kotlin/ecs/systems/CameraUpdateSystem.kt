package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.CameraFollowComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class CameraUpdateSystem(
    private val camera: OrthographicCamera = inject()) :
    IteratingSystem(allOf(
        CameraFollowComponent::class,
        TransformComponent::class).get()) {

    private val transformMapper = mapperFor<TransformComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        camera.position.set(transformMapper.get(entity).position, 0f)
    }
}