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
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import map.grid.Coordinate
import map.grid.GridMapSection
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
    val cursorColor = Color(0f, 1f, 0f, 0.5f)

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        Draw a cursor at tile that is in the direction the player is facing, ie, where
        the aimvector is pointing. And the aimvector should ALWAYS be pointing somewhere,
        which we shall fix NOW.
         */
        val position = entity.getComponent<TransformComponent>()
        val controlComponent = entity.getComponent<PlayerControlComponent>()
        val offset = CompassDirection.directionOffsets[controlComponent.aimVector.compassDirection()]!!
        coordinateToPaint.x = position.tileX + offset.x
        coordinateToPaint.y = position.tileY + offset.y

        /*
        Now, let's just draw a green square right there on that spot, to begin with.
         */
        shapeDrawer.batch.use {
            shapeDrawer.filledRectangle(
                coordinateToPaint.x.tileWorldX(),
                coordinateToPaint.y.tileWorldY(),
                GridMapSection.tileWidth,
                GridMapSection.tileHeight,
                cursorColor
            )
        }
    }
}

fun Vector2.compassDirection(): CompassDirection {
    return when (this.angleDeg()) {
        in 248f..293f -> CompassDirection.North
        in 293f..338f -> CompassDirection.NorthWest
        in 338f..360f -> CompassDirection.West
        in 0f..23f -> CompassDirection.West
        in 23f..68f -> CompassDirection.SouthWest
        in 68f..113f -> CompassDirection.South
        in 113f..158f -> CompassDirection.SouthEast
        in 158f..203f -> CompassDirection.East
        in 203f..248f -> CompassDirection.NorthEast
        else -> CompassDirection.South
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