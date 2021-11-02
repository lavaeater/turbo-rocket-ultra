package ecs.systems.fx

import box2dLight.RayHandler
import com.badlogic.ashley.core.EntitySystem

class RenderBox2dLightSystem(private val rayHandler: RayHandler) : EntitySystem() {
    override fun update(deltaTime: Float) {
        rayHandler.updateAndRender()
    }
}