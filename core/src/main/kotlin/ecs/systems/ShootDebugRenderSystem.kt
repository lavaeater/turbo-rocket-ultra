package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import injection.Context
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import physics.isEnemy
import physics.isEntity
import space.earlygrey.shapedrawer.ShapeDrawer
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture


class ShootDebugRenderSystem() : IteratingSystem(
    allOf(
        TransformComponent::class,
        PlayerControlComponent::class
    ).get()
) {

    private val controlMapper = mapperFor<PlayerControlComponent>()
    private val transformMapper = mapperFor<TransformComponent>()

    private val batch: Batch by lazy { inject<PolygonSpriteBatch>() }
    private val textureRegion: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
    }
    private val shapeDrawer: ShapeDrawer by lazy { ShapeDrawer(batch, textureRegion) }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = controlMapper[entity]
        if(controlComponent.firing && controlComponent.lastShot <= 0f) {
            val transform = transformMapper[entity]
            batch.begin()
                shapeDrawer.line(transform.position, controlComponent.latestHitPoint, 0.1f)
            batch.end()
        }
    }

}