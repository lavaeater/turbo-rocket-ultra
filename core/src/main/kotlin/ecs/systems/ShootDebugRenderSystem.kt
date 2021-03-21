package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ecs.components.PlayerControlComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import space.earlygrey.shapedrawer.ShapeDrawer


class ShootDebugRenderSystem(private val debug: Boolean = false) : IteratingSystem(
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
    private var drawShot = true

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = controlMapper[entity]
        //
        val transform = transformMapper[entity]
        if (controlComponent.drawShot) {
            batch.begin()
            shapeDrawer.line(transform.position, controlComponent.latestHitPoint, Color.GREEN, 0.1f)
            batch.end()
        }
        if(debug) {
            batch.begin()
            shapeDrawer.line(transform.position, controlComponent.aimVector, Color.BLUE, 0.05f)
            shapeDrawer.line(transform.position, controlComponent.mousePosition, Color.RED, 0.05f)
            batch.end()
        }
    }

}