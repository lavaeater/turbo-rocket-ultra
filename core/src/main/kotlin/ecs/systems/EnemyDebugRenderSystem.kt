package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ecs.components.EnemyComponent
import ecs.components.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.graphics.use
import space.earlygrey.shapedrawer.ShapeDrawer

class EnemyDebugRenderSystem : IteratingSystem(
    allOf(
        EnemyComponent::class,
        TransformComponent::class).get()) {

    private val batch: Batch by lazy { inject<PolygonSpriteBatch>() }
    private val enemyMapper = mapperFor<EnemyComponent>()
    private val transformMapper = mapperFor<TransformComponent>()

    private val textureRegion: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.dispose()
        TextureRegion(texture, 0, 0, 1, 1)
    }
    private val shapeDrawer: ShapeDrawer by lazy { ShapeDrawer(batch, textureRegion) }

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(entity.has(enemyMapper)) {
            val enemyComponent = entity[enemyMapper]!!
            var color = Color.GREEN
            when (enemyComponent.state) {
                EnemyState.Ambling -> color = Color.GREEN
                EnemyState.Seeking -> color = Color.BLUE
                EnemyState.ChasePlayer -> color = Color.RED
            }
            shapeDrawer.filledCircle(entity[transformMapper]!!.position, 5f,color)
            if(enemyComponent.state == EnemyState.Seeking)
                shapeDrawer.line(enemyComponent.scanVectorStart, enemyComponent.scanVectorEnd, Color.RED, .1f)
        }
    }
}