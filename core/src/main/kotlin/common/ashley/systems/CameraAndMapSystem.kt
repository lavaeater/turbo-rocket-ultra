package common.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import eater.ecs.ashley.components.LDtkMap
import eater.ecs.ashley.components.TransformComponent
import ktx.ashley.allOf
import ktx.math.vec3

class CameraAndMapSystem(camera: OrthographicCamera, alpha: Float, private val extendViewport: ExtendViewport, private val aspectRatio:Float, private val useMapBounds: Boolean = false) :
    CameraFollowSystem(camera, alpha) {
    private val LDtkMapFamily = allOf(LDtkMap::class).get()
    private val mapEntity get() = engine.getEntitiesFor(LDtkMapFamily).firstOrNull()

    override fun processEntity(entity: Entity, deltaTime: Float) {


        if (useMapBounds && mapEntity != null) {
            val LDtkMap = LDtkMap.get(mapEntity!!)
            camera.position.set(LDtkMap.mapBounds.getCenter(cameraPosition), 0f)
            extendViewport.minWorldWidth =
                if (LDtkMap.mapBounds.width > LDtkMap.mapBounds.height) LDtkMap.mapBounds.width * 1.25f else extendViewport.minWorldHeight / aspectRatio
            extendViewport.minWorldHeight =
                if (LDtkMap.mapBounds.height > LDtkMap.mapBounds.width) LDtkMap.mapBounds.height * 1.25f else extendViewport.minWorldWidth / aspectRatio
            extendViewport.update(Gdx.graphics.width, Gdx.graphics.height)
        } else {
            val position = TransformComponent.get(entity).position
            cameraPosition.set(position)
        }

        camera.position.lerp(
            vec3(cameraPosition, 0f), alpha
        )

        camera.update(true)
    }

}