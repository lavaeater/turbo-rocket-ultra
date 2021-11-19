package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerControlComponent
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import ecs.systems.tileX
import ecs.systems.tileY
import factories.blockade
import factories.world
import injection.Context.inject
import ktx.ashley.allOf
import ktx.box2d.KtxQueryCallback
import ktx.box2d.query
import ktx.graphics.use
import map.grid.GridMapManager
import map.grid.GridMapSection.Companion.scaledHeight
import map.grid.GridMapSection.Companion.scaledWidth
import map.grid.GridMapSection.Companion.tileHeight
import map.grid.GridMapSection.Companion.tileScale
import map.grid.GridMapSection.Companion.tileWidth
import physics.drawScaled
import physics.getComponent
import physics.isEntity
import physics.isPlayer
import tru.Assets
import tru.SpriteDirection

//Should render after map, before entities, that's the best...
class BuildSystem(private val debug: Boolean = true) : IteratingSystem(
    allOf(
        TransformComponent::class,
        PlayerControlComponent::class
    ).get(), 1
) {
    val batch by lazy { inject<PolygonSpriteBatch>() }
    val shapeDrawer by lazy { Assets.shapeDrawer }
    val cursorColor = Color(0f, 1f, 0f, 0.3f)
    val otherColor = Color(1f, 0f, 0f, 0.3f)

    val buildables by lazy { Assets.buildables }
    val mapManager by lazy { inject<GridMapManager>() }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        if (controlComponent.isInBuildMode) {
            val position = entity.getComponent<TransformComponent>().position
            val offset = CompassDirection.directionOffsets[controlComponent.compassDirection]!!

            val texture = buildables.first()
            val cX = position.tileWorldX() + (offset.x * scaledWidth)
            val cY = position.tileWorldY() + (offset.y * scaledHeight)

            val tX = position.tileWorldX() + (offset.x * scaledWidth)// + texture.offsetX * tileScale / 2
            val tY = position.tileWorldY() + (offset.y * scaledHeight)// + texture.offsetY * tileScale / 2

            val bodyX = position.tileWorldX() + (offset.x * scaledWidth)
            val bodyY = position.tileWorldY() + (offset.y * scaledHeight)

            val pWidth = tileWidth * tileScale
            val pHeight = tileHeight * tileScale

            val qbX = tX + pWidth / 4
            val qbY = tY + pHeight / 4
            val qtX = qbX + pWidth / 2
            val qtY = qbY + pHeight / 2

            batch.use {
                batch.drawScaled(
                    texture,
                    cX,
                    cY,
                    tileScale
                )
            }
            shapeDrawer.batch.use {
                shapeDrawer.filledRectangle(
                    tX,
                    tY,
                    pWidth,
                    pHeight,
                    cursorColor
                )

            if (debug) {
               shapeDrawer.filledRectangle(
                        position.tileWorldX(),
                        position.tileWorldY(),
                        pWidth,
                        pHeight,
                        otherColor
                    )
                shapeDrawer.filledCircle(cX,cY,.5f, Color.RED)
                shapeDrawer.filledCircle(tX,tY,.5f, Color.GREEN)
                shapeDrawer.filledCircle(bodyX,bodyY,.5f, Color.BLUE)
                }


                shapeDrawer.rectangle(qbX,qbY, pWidth /2, pHeight / 2, Color.BLUE)


            }
            if(controlComponent.buildIfPossible) {
                var checker = true
                world().query(qbX,
                    qbY,qtX, qtY, object: KtxQueryCallback {
                        override fun invoke(fixture: Fixture): Boolean {
                            if(!fixture.isPlayer())
                                checker = false
                            return checker
                        }
                    })

                if(checker) {
                    blockade(tX + 2f, tY + 2f)
                }
            }
        }
    }
}

fun Vector2.spriteDirection(): SpriteDirection {
    return when (this.angleDeg()) {
        in 150f..209f -> SpriteDirection.East
        in 210f..329f -> SpriteDirection.North
        in 330f..360f -> SpriteDirection.West
        in 0f..29f -> SpriteDirection.West
        in 30f..149f -> SpriteDirection.South
        else -> SpriteDirection.South
    }
}