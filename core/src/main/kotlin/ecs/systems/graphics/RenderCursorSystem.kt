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
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import map.grid.Coordinate
import map.grid.GridMapSection.Companion.scaledHeight
import map.grid.GridMapSection.Companion.scaledWidth
import map.grid.GridMapSection.Companion.tileHeight
import map.grid.GridMapSection.Companion.tileScale
import map.grid.GridMapSection.Companion.tileWidth
import physics.getComponent
import tru.Assets
import tru.SpriteDirection

//Should render after map, before entities, that's the best...
class RenderCursorSystem : IteratingSystem(
    allOf(
        TransformComponent::class,
        PlayerControlComponent::class
    ).get(), 1
) {
    val batch by lazy { inject<PolygonSpriteBatch>() }
    val shapeDrawer by lazy { Assets.shapeDrawer }
    val coordinateToPaint = Coordinate(0, 0)
    val cursorColor = Color(0f, 1f, 0f, 0.1f)
    val playerColor = Color(1f, 0f, 0f, 0.1f)
    val tBlue = Color(0f, 0f, 1f, 0.1f)
    private val pixelsPerMeter = 16f //TODO: Move scale concept to one place
    private val scale = 1 / pixelsPerMeter
    val console by lazy { inject<GUIConsole>() }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        Draw a cursor at tile that is in the direction the player is facing, ie, where
        the aimvector is pointing. And the aimvector should ALWAYS be pointing somewhere,
        which we shall fix NOW.
         */
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        if (controlComponent.isBuilding) {
            val position = entity.getComponent<TransformComponent>().position
            val offset = CompassDirection.directionOffsets[controlComponent.compassDirection]!!

            val cX = position.tileWorldX() + (offset.x * scaledWidth)
            val cY = position.tileWorldY() + (offset.y * scaledHeight)

            val pWidth = tileWidth * tileScale
            val pHeight = tileHeight * tileScale

            shapeDrawer.batch.use {
                shapeDrawer.filledRectangle(
                    cX,
                    cY,
                    pWidth,
                    pHeight,
                    cursorColor
                )
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