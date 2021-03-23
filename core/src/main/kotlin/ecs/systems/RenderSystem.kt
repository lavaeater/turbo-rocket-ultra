package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ecs.components.CharacterSpriteComponent
import ecs.components.EnemyComponent
import ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.graphics.use
import ktx.scene2d.scene2d
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer

class RenderSystem(
    private val batch: Batch
) : IteratingSystem(
    allOf(
        TransformComponent::class,
        CharacterSpriteComponent::class
    ).get(), 0
) {

    private val pixelsPerMeter = 16f
    private val scale = 1 / pixelsPerMeter
    private var animationStateTime = 0f

    private val tMapper = mapperFor<TransformComponent>()
    private val sMapper = mapperFor<CharacterSpriteComponent>()
    private val enemyMapper= mapperFor<EnemyComponent>()

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
        animationStateTime+=deltaTime
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //1. Just render the texture without animation
        val transform = tMapper.get(entity)
        val spriteComponent = sMapper.get(entity)
        val currentTextureRegion = spriteComponent.currentAnim.getKeyFrame(animationStateTime)
        if(entity.has(enemyMapper)) {
            val enemyComponent = entity[enemyMapper]!!
            var color = Color.GREEN
            when (enemyComponent.state) {
                EnemyState.Ambling -> color = Color.GREEN
                EnemyState.Seeking -> color = Color.BLUE
                EnemyState.ChasePlayer -> color = Color.RED
            }
            shapeDrawer.filledCircle(transform.position, 5f,color)
        }
        batch.drawScaled(
            currentTextureRegion,
            (transform.position.x + (currentTextureRegion.regionWidth / 2 * scale)),
            (transform.position.y + (currentTextureRegion.regionHeight * scale / 5)),
            scale
        )

//        for (obj in spriteComponent.objectsToDraw)
//            batch.drawScaled(
//                obj.currentTextureRegion,
//                (transform.position.x + (obj.currentTextureRegion.regionWidth / 2 * scale)),
//                (transform.position.y + (obj.currentTextureRegion.regionHeight * scale / 5)),
//                scale
//            )
//
//        for ((name, sprites) in Assets.objectSprites) {
//            batch.draw(sprites.values.first(), transform.position.x, transform.position.y)
//        }
    }
}


