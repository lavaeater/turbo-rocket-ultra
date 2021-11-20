package ecs.systems.graphics

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World


class PhysicsDebugRendererSystem(
    private val world: World,
    private val camera: OrthographicCamera) : EntitySystem(4) {

    private val debugRenderer = Box2DDebugRenderer()

    override fun update(deltaTime: Float) {
        debugRenderer.render(world, camera.combined)
    }
}

