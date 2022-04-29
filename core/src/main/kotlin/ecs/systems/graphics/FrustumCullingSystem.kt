package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.SpriteComponent
import injection.Context
import ktx.ashley.allOf
import physics.sprite
import physics.transform

/**
 * Cool thought, if this is slow:
 * Two systems. One checks only entities that we know have visiblecomponent, the other
 * checks entities with notvisiblecomponent
 */
class FrustumCullingSystem : IteratingSystem(allOf(TransformComponent::class, SpriteComponent::class).get()) {
    private val camera by lazy { Context.inject<OrthographicCamera>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = entity.transform().position
        //entity.sprite().isVisible = camera.frustum.pointInFrustum(position.x, position.y, 0f)
    }

}