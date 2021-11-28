package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.InFrustumComponent
import ecs.components.graphics.TextureComponent
import injection.Context
import ktx.ashley.allOf
import ktx.ashley.remove
import physics.AshleyMappers
import physics.addComponent
import physics.getComponent

/**
 * Cool thought, if this is slow:
 * Two systems. One checks only entities that we know have visiblecomponent, the other
 * checks entities with notvisiblecomponent
 */
class FrustumCullingSystem : IteratingSystem(allOf(TransformComponent::class, TextureComponent::class).get()) {
    private val camera by lazy { Context.inject<OrthographicCamera>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = AshleyMappers.transform.get(entity).position
        if (camera.frustum.pointInFrustum(position.x, position.y, 0f)) {
            entity.addComponent<InFrustumComponent> { }
        } else {
            entity.remove<InFrustumComponent>()
        }
    }

}