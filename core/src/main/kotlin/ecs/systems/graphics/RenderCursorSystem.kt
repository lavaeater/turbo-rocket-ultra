package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import com.strongjoshua.console.GUIConsole
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerControlComponent
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import factories.player
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import map.grid.Coordinate
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
class RenderCursorSystem(private val debug: Boolean = true) : IteratingSystem(
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

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        if (controlComponent.isBuilding) {
            val position = entity.getComponent<TransformComponent>().position
            val offset = CompassDirection.directionOffsets[controlComponent.compassDirection]!!

            val texture = buildables.first()
            val cX = position.tileWorldX() + (offset.x * scaledWidth) + texture.offsetX * tileScale
            val cY = position.tileWorldY() + (offset.y * scaledHeight) + texture.offsetY * tileScale

            val tX = position.tileWorldX() + (offset.x * scaledWidth)// + texture.offsetX * tileScale / 2
            val tY = position.tileWorldY() + (offset.y * scaledHeight)// + texture.offsetY * tileScale / 2

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