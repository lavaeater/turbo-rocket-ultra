package map.grid

import box2dLight.ConeLight
import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.graphics.renderables.RenderableTextureRegions
import factories.world
import injection.Context.inject
import ktx.box2d.Query
import ktx.box2d.query
import ktx.math.vec2
import map.snake.*
import physics.getEntity
import physics.hasObstacle
import physics.isEntity
import tru.Assets

class GridMapSection(
    val coordinate: Coordinate,
    val connections: Set<MapDirection>,
    val startSection: Boolean = false
) {
    val x get() = coordinate.x
    val y get() = coordinate.y
    val sectionWidth = width * tileWidth * tileScale
    val sectionHeight = height * tileHeight * tileScale
    val sectionOffsetX = x * sectionWidth - tileWidth * tileScale
    val sectionOffsetY = y * sectionHeight - tileHeight * tileScale
    val bounds by lazy {
        Rectangle(
            sectionOffsetX,
            sectionOffsetY,
            sectionWidth,
            sectionHeight
        )
    }
    val innerBounds by lazy {
        Rectangle(
            bounds.left() + tileWidth * tileScale * 2,
            bounds.bottom() + tileHeight * tileScale * 2,
            bounds.width - tileWidth * tileScale * 4,
            bounds.height - tileHeight * tileScale * 4
        )
    }

    /**
     * These bounds can be used to
     * spawn things safely within them
     */
    val safeBounds by lazy {
        Rectangle(
            innerBounds.left() - tileWidth * tileScale,
            innerBounds.bottom() + tileHeight * tileScale,
            innerBounds.width,
            innerBounds.height
        )
    }

    val safePoints by lazy {
        val sp = mutableListOf<Vector2>()
        val maxPoints = (innerBounds.area()).toInt() / 10
        for (i in 0..maxPoints) {
            val point = innerBounds.randomPoint()
            if (isThisPointSafe(point)) {
                sp.add(point)
            }
        }
        sp
    }

    private fun isThisPointSafe(position: Vector2): Boolean {
        var itIsSafe = true
        world().query(position.x - 0.5f, position.y - 0.5f, position.x + 0.5f, position.y + 0.5f) {
            if (it.isEntity() && it.getEntity().hasObstacle()) {
                itIsSafe = false
                Query.STOP
            }
            Query.CONTINUE
        }
        return itIsSafe
    }

    val lights by lazy {
        rayHandler.setShadows(true)
        val lightDirections = MapDirection.directions.filter { !connections.contains(it) }

        lightDirections.map { lightDirection ->
            val lightPosition = vec2()
            when (lightDirection) {
                MapDirection.North -> lightPosition.set(innerBounds.horizontalCenter(), innerBounds.bottom())
                MapDirection.East -> lightPosition.set(innerBounds.right(), innerBounds.verticalCenter())
                MapDirection.South -> lightPosition.set(innerBounds.horizontalCenter(), innerBounds.top())
                MapDirection.West -> lightPosition.set(innerBounds.left(), innerBounds.verticalCenter())
            }
            ConeLight(
                rayHandler,
                32,
                directionColorMap[lightDirection]!!,//Color(.05f, .05f, .05f, 1f),
                30f,
                lightPosition.x,
                lightPosition.y,
                MapDirection.directionDegrees[lightDirection]!!,
                90f
            ).apply {
                isStaticLight = false
                isSoft = true
            }
        }
    }

    companion object {
        val width = 6
        val height = 6
        val tileWidth = 16f
        val tileHeight = 16f
        val tileScale = 1 / 4f
        val scaledWidth = tileWidth * tileScale
        val scaledHeight = tileHeight * tileScale
        val directionAlignment by lazy {
            mapOf(
                MapDirection.West to listOf(TileAlignment.Left, TileAlignment.TopLeft),
                MapDirection.North to listOf(TileAlignment.Top),
                MapDirection.East to listOf(TileAlignment.Right, TileAlignment.TopRight),
                MapDirection.South to listOf(TileAlignment.Bottom)
            )
        }
        val directionColorMap = mapOf(
            MapDirection.North to Color(.5f, .5f, 0f, .5f),
            MapDirection.East to Color(.5f, .5f, 0f, .5f),
            MapDirection.South to Color(.5f, .5f, 0f, .5f),
            MapDirection.West to Color(.5f, .5f, 0f, .5f)
        )
    }

    val connectionAlignments by lazy { connections.map { directionAlignment[it]!! }.flatten() }
    val rayHandler by lazy { inject<RayHandler>() }

    val tiles by lazy {
        Array(width) { x ->
            Array(height) { y ->
                val tileAlignment = when (x) {
                    0 -> when (y) {
                        0 -> TileAlignment.TopLeft
                        height - 1 -> TileAlignment.BottomLeft
                        else -> TileAlignment.Left
                    }
                    width - 1 -> when (y) {
                        0 -> TileAlignment.TopRight
                        height - 1 -> TileAlignment.BottomRight
                        else -> TileAlignment.Right
                    }
                    else -> when (y) {
                        0 -> TileAlignment.Top
                        height - 1 -> TileAlignment.Bottom
                        else -> TileAlignment.Center
                    }
                }

                return@Array when (tileAlignment) {
                    TileAlignment.Bottom -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.BottomLeft -> if (connectionAlignments.contains(TileAlignment.Left) && connectionAlignments.contains(
                            TileAlignment.Bottom
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.BottomRight -> if (connectionAlignments.contains(TileAlignment.Right) && connectionAlignments.contains(
                            TileAlignment.Bottom
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.Center -> MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    )
                    TileAlignment.Left -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.Right -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.Top -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallEndTile))
                        ), false
                    )
                    TileAlignment.TopLeft -> if (connectionAlignments.contains(TileAlignment.Left) && connectionAlignments.contains(
                            TileAlignment.Top
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallEndTile))
                        ), false
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.TopRight -> if (connectionAlignments.contains(TileAlignment.Right) && connectionAlignments.contains(
                            TileAlignment.Top
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallEndTile))
                        ), false
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                }
            }
        }
    }
}