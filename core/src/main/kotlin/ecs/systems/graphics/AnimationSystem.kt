package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.SpriteComponent
import ktx.ashley.allOf
import physics.animation
import physics.sprite

class AnimationSystem : IteratingSystem(allOf(SpriteComponent::class, AnimatedCharacterComponent::class).get()) {
    private var animationStateTime = 0f

    override fun update(deltaTime: Float) {
        animationStateTime += deltaTime
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = entity.animation()
        animationComponent.currentAnim = animationComponent.anims[animationComponent.currentAnimState]!!.animations[animationComponent.currentDirection]!!
        val spriteComponent = entity.sprite()
        spriteComponent.sprite = animationComponent.currentAnim.getKeyFrame(animationStateTime)
    }
}