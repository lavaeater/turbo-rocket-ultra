package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.TextureComponent
import ecs.components.graphics.renderables.AnimatedCharacterComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.graphics.use
import physics.drawScaled

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
        animationComponent.animationStateTime = animationStateTime
        animationComponent.currentAnim = animationComponent.anims[animationComponent.currentAnimState]!!.animations[animationComponent.currentDirection]!!
        val textureComponent = textureMapper.get(entity)
        textureComponent.texture = animationComponent.currentTextureRegion
    }
}

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
            transform.position.x + textureComponent.xOffset,
            transform.position.y + textureComponent.yOffset,
            scale)
        for(texture in textureComponent.extraTextures) {
            batch.drawScaled(
                texture,
                transform.position.x + texture.offsetX,
                transform.position.y + texture.offsetY
            )
        }
    }
}