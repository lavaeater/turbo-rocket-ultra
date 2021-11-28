package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.InFrustumComponent
import ecs.components.graphics.InLineOfSightComponent
import ecs.components.graphics.TextureComponent
import ecs.systems.graphics.GameConstants.scale
import ktx.ashley.allOf
import ktx.graphics.use
import physics.AshleyMappers
import physics.drawScaled
import physics.getComponent
import tru.Assets

class RenderSystem(
    private val batch: Batch, private val debug: Boolean
) : SortedIteratingSystem(
    allOf(
        TransformComponent::class,
        TextureComponent::class,
        InFrustumComponent::class
    ).get(),
    object : Comparator<Entity> {
        override fun compare(p0: Entity, p1: Entity): Int {
            val layer0 = AshleyMappers.texture.get(p0).layer
            val layer1 = AshleyMappers.texture.get(p1).layer
            return if (layer0 == layer1) {
                val y0 = (AshleyMappers.transform.get(p0).position.y)
                val y1 = (AshleyMappers.transform.get(p1).position.y)
                val compareVal = y0.compareTo(y1)
                if (compareVal != 0)
                    return compareVal
                else {
                    val x0 = AshleyMappers.transform.get(p0).position.y
                    val x1 = AshleyMappers.transform.get(p1).position.y
                    x1.compareTo(x0)
                }
            } else {
                layer0.compareTo(layer1)
            }
        }
    }, 8) {
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    override fun update(deltaTime: Float) {
        forceSort()
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = AshleyMappers.transform.get(entity)
        val textureComponent = AshleyMappers.texture.get(entity)

        batch.drawScaled(
            textureComponent.texture,
            transform.position.x + (textureComponent.texture.regionWidth / 2  + textureComponent.offsetX)* scale * textureComponent.scale,
            transform.position.y + (textureComponent.texture.regionHeight / 2 + textureComponent.offsetY) * scale * textureComponent.scale,
            scale * textureComponent.scale,
            if (textureComponent.rotateWithTransform) transform.rotation else 180f
        )
        for (texture in textureComponent.extraTextures.values) {
            batch.drawScaled(
                texture.first,
                transform.position.x + (textureComponent.texture.regionWidth / 2 + textureComponent.offsetX) * scale * textureComponent.scale,
                transform.position.y + (textureComponent.texture.regionHeight / 2 + textureComponent.offsetY) * scale * textureComponent.scale,
                scale * textureComponent.scale * texture.second,
                if (textureComponent.rotateWithTransform) transform.rotation else 180f
            )
        }
        if(debug) {
            shapeDrawer.filledCircle(transform.position, .2f, Color.RED)
        }
    }
}

