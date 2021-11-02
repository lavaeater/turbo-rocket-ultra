package ecs.systems.fx

import box2dLight.RayHandler
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera

class RenderBox2dLightSystem(private val rayHandler: RayHandler, private val camera: OrthographicCamera) : EntitySystem() {
    override fun update(deltaTime: Float) {
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender()
    }
}