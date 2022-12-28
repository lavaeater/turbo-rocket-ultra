package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import eater.ecs.ashley.components.TransformComponent
import eater.injection.InjectionContext.Companion.inject
import ecs.components.graphics.TextureRegionComponent
import ktx.ashley.allOf
import physics.transform

/**
 * Cool thought, if this is slow:
 * Two systems. One checks only entities that we know have visiblecomponent, the other
 * checks entities with notvisiblecomponent
 */
class FrustumCullingSystem : IteratingSystem(allOf(TransformComponent::class, TextureRegionComponent::class).get()) {
    private val camera by lazy { inject<OrthographicCamera>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = entity.transform().position
        //entity.sprite().isVisible = camera.frustum.pointInFrustum(position.x, position.y, 0f)
    }

}