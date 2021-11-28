package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.TextureComponent
import ecs.components.graphics.AnimatedCharacterComponent
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.getComponent

class AnimationSystem: IteratingSystem(allOf(TextureComponent::class, AnimatedCharacterComponent::class).get(),2) {
    private var animationStateTime = 0f

    override fun update(deltaTime: Float) {
        animationStateTime += deltaTime
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = AshleyMappers.animatedCharacter.get(entity)
        animationComponent.currentAnim = animationComponent.anims[animationComponent.currentAnimState]!!.animations[animationComponent.currentDirection]!!
        val textureComponent = AshleyMappers.texture.get(entity)
        textureComponent.texture = animationComponent.currentAnim.getKeyFrame(animationStateTime)
    }
}