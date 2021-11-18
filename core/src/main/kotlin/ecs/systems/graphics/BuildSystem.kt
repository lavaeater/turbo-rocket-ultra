package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerControlComponent
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import ecs.systems.tileX
import ecs.systems.tileY
import factories.blockade
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import map.grid.GridMapManager
import map.grid.GridMapSection.Companion.scaledHeight
import map.grid.GridMapSection.Companion.scaledWidth
import map.grid.GridMapSection.Companion.tileHeight
import map.grid.GridMapSection.Companion.tileScale
import map.grid.GridMapSection.Companion.tileWidth
import physics.drawScaled
import physics.getComponent
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
            val bodyY = position.tileWorldY() + (offset.y * scaledHeight)w

            val pWidth = tileWidth * tileScale
            val pHeight = tileHeight * tileScale
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
                shapeDrawer.filledCircle(cX,cY,1f, Color.RED)
                shapeDrawer.filledCircle(tX,tY,1f, Color.GREEN)
                shapeDrawer.filledCircle(bodyX,bodyY,1f, Color.BLUE)
                }
            }
            if(controlComponent.buildIfPossible) {
                if(mapManager.canWeBuildAt(cX.tileX(), cY.tileY())) {
                    blockade(bodyX, bodyY)
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