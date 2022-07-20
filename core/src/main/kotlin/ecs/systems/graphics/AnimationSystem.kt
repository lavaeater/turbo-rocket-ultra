package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.TextureRegionComponent
import ktx.ashley.allOf
import physics.animation
import physics.textureRegionComponent

class AnimationSystem : IteratingSystem(allOf(TextureRegionComponent::class, AnimatedCharacterComponent::class).get()) {
    private var animationStateTime = 0f

    override fun update(deltaTime: Float) {
        animationStateTime += deltaTime
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = entity.animation()
        animationComponent.currentAnim = animationComponent.anims[animationComponent.currentAnimState]!!.animations[animationComponent.currentDirection]!!
        val spriteComponent = entity.textureRegionComponent()
        spriteComponent.textureRegion = animationComponent.currentAnim.getKeyFrame(animationStateTime)
    }
}