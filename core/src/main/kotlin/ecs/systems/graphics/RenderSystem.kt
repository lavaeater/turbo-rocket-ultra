package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.BloomEffect
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.crashinvaders.vfx.effects.CrtEffect
import com.crashinvaders.vfx.effects.OldTvEffect
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.OnScreenComponent
import ecs.components.graphics.SpriteComponent
import ecs.systems.graphics.GameConstants.scale
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import map.grid.GridMapManager
import physics.*
import tru.Assets

class RenderSystem(
    private val batch: Batch, private val debug: Boolean
) : SortedIteratingSystem(
    allOf(
        TransformComponent::class,
        SpriteComponent::class,
        OnScreenComponent::class
    ).get(),
    object : Comparator<Entity> {
        override fun compare(p0: Entity, p1: Entity): Int {
            val layer0 = p0.sprite().layer
            val layer1 = p1.sprite().layer
            return if (layer0 == layer1) {
                val y0 = p0.transform().position.y
                val y1 = p1.transform().position.y
                val compareVal = y0.compareTo(y1)
                if (compareVal != 0)
                    return compareVal
                else {
                    val x0 = p0.transform().position.y
                    val x1 = p1.transform().position.y
                    x1.compareTo(x0)
                }
            } else {
                layer0.compareTo(layer1)
            }
        }
    }, 8
) {
    private val mapManager by lazy { inject<GridMapManager>() }
    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val oldTvEffect by lazy { inject<List<ChainVfxEffect>>() }
    private val vfxManager by lazy { inject<VfxManager>().apply {
        for(fx in oldTvEffect) {
            this.addEffect(fx)
        }
    }}

    override fun update(deltaTime: Float) {
        forceSort()

        vfxManager.cleanUpBuffers()
        vfxManager.beginInputCapture()
        batch.use {
            mapManager.render(batch, shapeDrawer, deltaTime)
            super.update(deltaTime)
        }
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
    }

    /*

     */
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        val spriteComponent = entity.sprite()

        batch.drawScaled(
            spriteComponent.sprite,
            transform.position.x + (spriteComponent.sprite.regionWidth / 2 + spriteComponent.offsetX) * scale * spriteComponent.scale,
            transform.position.y + (spriteComponent.sprite.regionHeight / 2 + spriteComponent.offsetY) * scale * spriteComponent.scale,
            scale * spriteComponent.scale,
            if (spriteComponent.rotateWithTransform) transform.rotation else 180f
        )
        if(debug) {
            shapeDrawer.filledCircle(
                transform.position.x + spriteComponent.sprite.originX * scale * spriteComponent.scale,
                transform.position.y + spriteComponent.sprite.originY * scale * spriteComponent.scale,
                .2f,
                Color.BLUE
            )
            shapeDrawer.filledCircle(
                transform.position.x,
                transform.position.y,
                .2f,
                Color.RED
            )
        }

        for ((key, sprite) in spriteComponent.extraSprites) {
            if (spriteComponent.extraSpriteAnchors.contains(key)) {
                val anchors = entity.anchors()
                val drawPosition = anchors.transformedPoints[spriteComponent.extraSpriteAnchors[key]!!]!!

                sprite.setOriginBasedPosition(drawPosition.x, drawPosition.y)
                sprite.rotation = if (anchors.useDirectionVector) entity.playerControl().directionVector.angleDeg() else transform.rotation * MathUtils.radiansToDegrees
                sprite.setScale(scale * spriteComponent.scale)
                sprite.draw(batch)

            } else {
                batch.drawScaled(
                    sprite,
                    transform.position.x + (spriteComponent.sprite.regionWidth / 2 + spriteComponent.offsetX) * scale * spriteComponent.scale,
                    transform.position.y + (spriteComponent.sprite.regionHeight / 2 + spriteComponent.offsetY) * scale * spriteComponent.scale,
                    scale * spriteComponent.scale,
                    if (spriteComponent.rotateWithTransform) transform.rotation else 180f
                )
            }
        }
        if (debug) {
            shapeDrawer.filledCircle(transform.position, .2f, Color.RED)
        }
    }
}

