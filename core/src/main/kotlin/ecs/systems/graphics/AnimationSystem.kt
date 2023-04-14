package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.TextureRegionComponent
import ktx.ashley.allOf

class AnimationSystem : IteratingSystem(allOf(TextureRegionComponent::class, AnimatedCharacterComponent::class).get()) {
    private var animationStateTime = 0f

    override fun update(deltaTime: Float) {
        animationStateTime += deltaTime
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = AnimatedCharacterComponent.get(entity)
        if (
            animationComponent
                .anims
                .containsKey(animationComponent.currentAnimState) &&
            animationComponent
                .anims[animationComponent.currentAnimState]!!
                .animations.containsKey(
                    animationComponent.currentDirection
                )
        ) {
            animationComponent.currentAnim =
                animationComponent
                    .anims[animationComponent.currentAnimState]!!
                    .animations[animationComponent.currentDirection]!!
            val spriteComponent = TextureRegionComponent.get(entity)
            spriteComponent.textureRegion =
                animationComponent
                    .currentAnim
                    .getKeyFrame(animationStateTime)
        } else {
            val what = "what the hell?"
        }
    }
}