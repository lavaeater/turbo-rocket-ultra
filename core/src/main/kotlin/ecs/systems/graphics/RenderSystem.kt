package ecs.systems.graphics

import box2dLight.RayHandler
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.ChainVfxEffect
import ecs.components.gameplay.DestroyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.RenderableComponent
import ecs.systems.graphics.GameConstants.SCALE
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import map.grid.GridMapManager
import physics.*
import tru.Assets

class RenderSystem(
    private val batch: Batch,
    private val debug: Boolean,
    private val rayHandler: RayHandler,
    private val camera: OrthographicCamera,
    private val mainViewPort: Viewport,
    private val enemyDebug: Boolean,
    priority: Int,
    val playerDebug: Boolean
) : SortedIteratingSystem(
    allOf(
        RenderableComponent::class,
        TransformComponent::class
    ).get(),
    object : Comparator<Entity> {
        override fun compare(p0: Entity, p1: Entity): Int {
            val layer0 = p0.renderable().layer
            val layer1 = p1.renderable().layer
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
    }, priority
) {
    private val mapManager by lazy { inject<GridMapManager>() }
    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val oldTvEffect by lazy { inject<List<ChainVfxEffect>>() }
    private val vfxManager by lazy {
        inject<VfxManager>().apply {
            for (fx in oldTvEffect) {
                this.addEffect(fx)
            }
        }
    }
    private val colorMap =
        mutableMapOf("blue" to Color.BLUE, "red" to Color.RED, "green" to Color.GREEN, "yellow" to Color.YELLOW)
    private val r = 0f..1f

    override fun update(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update(false) //True or false, what's the difference?
        batch.projectionMatrix = camera.combined
        forceSort()
        rayHandler.setCombinedMatrix(camera)

        vfxManager.cleanUpBuffers()
        vfxManager.beginInputCapture()
        batch.use {
            mapManager.render(batch, shapeDrawer, deltaTime)
            super.update(deltaTime)
        }
        vfxManager.endInputCapture()
        vfxManager.applyEffects()
        vfxManager.renderToScreen()
        rayHandler.updateAndRender()
    }

    fun renderSpriteEntity(entity: Entity) {
        val transform = entity.transform()
        val spriteComponent = entity.sprite()

        if (spriteComponent.isVisible) {
            val sprite = spriteComponent.sprite
            if (spriteComponent.rotateWithTransform)
                sprite.rotation = transform.rotation * MathUtils.radiansToDegrees
            sprite.setOriginBasedPosition(transform.position.x + spriteComponent.actualOffsetX, transform.position.y + spriteComponent.actualOffsetY)

            sprite.draw(batch)
            if (debug) {
                shapeDrawer.filledCircle(
                    sprite.originX,
                    sprite.originY, .5f,
                    Color.GREEN

                )
                shapeDrawer.filledCircle(
                    transform.position.x + spriteComponent.sprite.originX * SCALE * spriteComponent.scale,
                    transform.position.y + spriteComponent.sprite.originY * SCALE * spriteComponent.scale,
                    .5f,
                    Color.RED
                )
                shapeDrawer.filledCircle(
                    transform.position.x,
                    transform.position.y,
                    .5f,
                    Color.WHITE
                )
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        when (entity.renderable().renderableType) {
            is ecs.components.graphics.RenderableType.Sprite -> renderSpriteEntity(entity)
            is ecs.components.graphics.RenderableType.Effect -> renderEffect(entity, deltaTime)
        }

        if (enemyDebug && entity.isEnemy()) {
            val ec = entity.agentProps()
            val previous = entity.transform().position.cpy()
            shapeDrawer.line(previous, ec.nextPosition, Color.BLUE, 0.1f)
            previous.set(ec.nextPosition)
            for ((i, node) in ec.path.withIndex()) {
                shapeDrawer.line(previous, node, lineColor, 0.1f)
                when (i) {
                    0 -> {
                        shapeDrawer.filledCircle(node, .25f, Color.GREEN)
                    }
                    ec.path.size - 1 -> {
                        shapeDrawer.filledCircle(node, .25f, Color.RED)
                    }
                    else -> {
                        shapeDrawer.filledCircle(node, .25f, Color.BLUE)
                    }
                }
                previous.set(node)
            }
        }

        if(playerDebug && entity.isPlayer() && entity.hasAnchors()) {
            for((key, point) in entity.anchors().transformedPoints) {
                shapeDrawer.filledCircle(point, 0.2f, if(colorMap.containsKey(key)) colorMap[key] else Color.WHITE)
            }
        }
    }

    private fun renderEffect(entity: Entity, deltaTime: Float) {
        if (entity.hasSplatter()) {
            val component = entity.splatterEffect()
            val effect = component.splatterEffect
            if (effect.isComplete) {
                engine.removeEntity(entity)
            }
            if (!component.started) {
                component.started = true
                val emitter = effect.emitters.first()
                emitter.setPosition(component.at.x, component.at.y)
                val amplitude: Float = (emitter.angle.highMax - emitter.angle.highMin) / 2f
                emitter.angle.setHigh(component.rotation + amplitude, component.rotation - amplitude)
                emitter.angle.setLow(component.rotation)
                emitter.start()
            }
            effect.update(deltaTime)
            effect.draw(batch)
        }

        if (entity.hasEffect()) {
            val effectComponent = entity.effect()
            if (effectComponent.ready) {
                val transform = entity.transform()
                val effect = effectComponent.effect
                if (effect.isComplete) {
                    entity.addComponent<DestroyComponent>()
                }
                for (emitter in effect.emitters) {
                    emitter.setPosition(transform.position.x, transform.position.y)
                    if (!effectComponent.started) {
                        val amplitude: Float = (emitter.angle.highMax - emitter.angle.highMin) / 2f
                        emitter.angle.setHigh(
                            effectComponent.rotation + amplitude,
                            effectComponent.rotation - amplitude
                        )
                        emitter.angle.setLow(effectComponent.rotation)
                        emitter.start()
                    }
                }
                if (!effectComponent.started) {
                    effectComponent.started = true
                }
                effect.update(deltaTime)
                effect.draw(batch)
            }
        }
    }

    val lineColor = Color(0f, 0f, 1f, 0.5f)
}

