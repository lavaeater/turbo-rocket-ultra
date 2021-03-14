package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ecs.components.CharacterSpriteComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.graphics.use
import physics.getComponent

class RenderSystem(
    private val spriteBatch: Batch,
    private val camera: OrthographicCamera) : IteratingSystem(
    allOf(
        TransformComponent::class,
        CharacterSpriteComponent::class).get()) {

    private val metersPerPixel = 1f

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        //1. Just render the texture without animation
        spriteBatch.projectionMatrix = camera.combined
        val transform = entity.getComponent<TransformComponent>()
        val currentTextureRegion = entity.getComponent<CharacterSpriteComponent>().currentAnim.keyFrames.first()
        spriteBatch.use {
            spriteBatch.drawScaled(
                currentTextureRegion,
                (transform.position.x - (currentTextureRegion.regionWidth / 2 * metersPerPixel)),
                (transform.position.y - (currentTextureRegion.regionHeight / 2 * metersPerPixel)),
            )
        }
    }
}


fun Batch.drawScaled(
    textureRegion: TextureRegion,
    x: Float,
    y: Float,
    scale: Float = 1f,
    rotation: Float = 0f) {

    draw(
        textureRegion,
        x,
        y,
        0f,
        0f,
        textureRegion.regionWidth.toFloat(),
        textureRegion.regionHeight.toFloat(),
        scale,
        scale,
        rotation)
}