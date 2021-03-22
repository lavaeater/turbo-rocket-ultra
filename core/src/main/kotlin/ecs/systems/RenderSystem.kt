package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.CharacterSpriteComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import physics.drawScaled

class RenderSystem(
    private val batch: Batch
) : IteratingSystem(
    allOf(
        TransformComponent::class,
        CharacterSpriteComponent::class
    ).get(), 0
) {

    private val pixelsPerMeter = 64f
    private val scale = 1 / pixelsPerMeter
    private var animationStateTime = 0f

    private val tMapper = mapperFor<TransformComponent>()
    private val sMapper = mapperFor<CharacterSpriteComponent>()

    override fun update(deltaTime: Float) {
        animationStateTime+=deltaTime
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //1. Just render the texture without animation
        val transform = tMapper.get(entity)
        val currentTextureRegion = sMapper.get(entity).currentAnim.getKeyFrame(animationStateTime)
        batch.drawScaled(
            currentTextureRegion,
            (transform.position.x + (currentTextureRegion.regionWidth / 2 * scale)),
            (transform.position.y + (currentTextureRegion.regionHeight * scale / 5)),
            scale
        )
    }
}


