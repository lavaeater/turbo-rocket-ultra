package common.ashley.systems


import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import eater.ecs.ashley.components.CameraFollow
import eater.ecs.ashley.components.TransformComponent
import ktx.ashley.allOf
import ktx.math.vec2
import ktx.math.vec3

open class CameraFollowSystem(
    protected val camera: OrthographicCamera,
    protected val alpha: Float
) :
    IteratingSystem(
        allOf(
            CameraFollow::class,
            TransformComponent::class
        ).get()
    ) {

    protected val cameraPosition = vec2()


    override fun processEntity(entity: Entity, deltaTime: Float) {

        val position = TransformComponent.get(entity).position
        cameraPosition.set(position)

        camera.position.lerp(
            vec3(cameraPosition, 0f), alpha
        )

        camera.update(true)



    }
}