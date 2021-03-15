package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.CharacterSpriteComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.graphics.use
import physics.drawScaled
import physics.getComponent

class RenderSystem(
    private val spriteBatch: Batch,
    private val camera: OrthographicCamera
) : IteratingSystem(
    allOf(
        TransformComponent::class,
        CharacterSpriteComponent::class
    ).get()) {

    private val metersPerPixel = .05f

    override fun update(deltaTime: Float) {
        spriteBatch.use {
            super.update(deltaTime)
        }
    }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        //1. Just render the texture without animation
        spriteBatch.projectionMatrix = camera.combined
        val transform = entity.getComponent<TransformComponent>()
        val currentTextureRegion = entity.getComponent<CharacterSpriteComponent>().currentAnim.keyFrames.first()
        spriteBatch.drawScaled(
            currentTextureRegion,
            (transform.position.x + (currentTextureRegion.regionWidth / 2 * metersPerPixel)),
            (transform.position.y + (currentTextureRegion.regionHeight * metersPerPixel / 5)),
            metersPerPixel
        )
    }
}


