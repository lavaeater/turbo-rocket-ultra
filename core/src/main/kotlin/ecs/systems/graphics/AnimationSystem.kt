package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.TextureComponent
import ecs.components.graphics.renderables.AnimatedCharacterComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class AnimationSystem: IteratingSystem(allOf(TextureComponent::class, AnimatedCharacterComponent::class).get(),2) {
    private var animationStateTime = 0f
    private val textureMapper = mapperFor<TextureComponent>()
    private val aniMapper = mapperFor<AnimatedCharacterComponent>()

    override fun update(deltaTime: Float) {
        animationStateTime += deltaTime
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = aniMapper.get(entity)
        animationComponent.currentAnim = animationComponent.anims[animationComponent.currentAnimState]!!.animations[animationComponent.currentDirection]!!
        val textureComponent = textureMapper.get(entity)
        textureComponent.texture = animationComponent.currentAnim.getKeyFrame(animationStateTime)
    }
}