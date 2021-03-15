package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.CharacterSpriteComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import physics.drawScaled

class RenderSystem(
    private val spriteBatch: Batch,
    private val camera: OrthographicCamera
) : IteratingSystem(
    allOf(
        TransformComponent::class,
        CharacterSpriteComponent::class
    ).get(), 0
) {

    private val pixelsPerMeter = 64f
    private val metersPerPixel = 1 / pixelsPerMeter

    private val tMapper = mapperFor<TransformComponent>()
    private val sMapper = mapperFor<CharacterSpriteComponent>()

    override fun update(deltaTime: Float) {
        spriteBatch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //1. Just render the texture without animation
        spriteBatch.projectionMatrix = camera.combined
        val transform = tMapper.get(entity)
        val currentTextureRegion = sMapper.get(entity).currentAnim.keyFrames.first()
        spriteBatch.drawScaled(
            currentTextureRegion,
            (transform.position.x + (currentTextureRegion.regionWidth / 2 * metersPerPixel)),
            (transform.position.y + (currentTextureRegion.regionHeight * metersPerPixel / 5)),
            metersPerPixel
        )
    }
}


