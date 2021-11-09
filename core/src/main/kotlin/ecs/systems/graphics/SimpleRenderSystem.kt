package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.TextureComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import physics.drawScaled

class SimpleRenderSystem(
    private val batch: Batch
): SortedIteratingSystem(
    allOf(
        TransformComponent::class,
        TextureComponent::class
    ).get(),
    object: Comparator<Entity> {
        val textureMapper = mapperFor<TextureComponent>()
        val transformMapper = mapperFor<TransformComponent>()

        override fun compare(p0: Entity, p1: Entity): Int {
            val layer0 = textureMapper.get(p0).layer
            val layer1 = textureMapper.get(p1).layer
            return if (layer0 == layer1) {
                val y0 = (transformMapper.get(p0).position.y)
                val y1 = (transformMapper.get(p1).position.y)
                val compareVal = y0.compareTo(y1)
                if (compareVal != 0)
                    return compareVal
                else {
                    val x0 = transformMapper.get(p0).position.y
                    val x1 = transformMapper.get(p1).position.y
                    x1.compareTo(x0)
                }
            } else {
                layer0.compareTo(layer1)
            }
        }
    }, 0) {
    private val transformMapper = mapperFor<TransformComponent>()
    private val textureMapper = mapperFor<TextureComponent>()
    private val pixelsPerMeter = 16f
    private val scale = 1 / pixelsPerMeter

    override fun update(deltaTime: Float) {
        forceSort()
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = transformMapper.get(entity)
        val textureComponent = textureMapper.get(entity)


        batch.drawScaled(
            textureComponent.texture,
            transform.position.x + (textureComponent.texture.regionWidth / 2 * scale) + (textureComponent.offsetX + textureComponent.texture.offsetX * scale * textureComponent.scale),
            transform.position.y + (textureComponent.texture.regionHeight / 2 * scale) - (textureComponent.offsetY - textureComponent.texture.offsetY) * scale * textureComponent.scale,
            scale * textureComponent.scale,
            if(textureComponent.rotateWithTransform) transform.rotation else 180f)
        for(texture in textureComponent.extraTextures.values) {
            batch.drawScaled(
                texture,
                transform.position.x + (textureComponent.texture.regionWidth / 2 * scale) + (textureComponent.offsetX + texture.offsetX) * scale * textureComponent.scale,
                transform.position.y + (textureComponent.texture.regionHeight / 2 * scale) - (textureComponent.offsetY - texture.offsetY) * scale * textureComponent.scale,
                scale * textureComponent.scale
            )
        }
    }
}