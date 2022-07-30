package map.grid

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import eater.core.world
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.graphics.renderables.RenderableTextureRegions
import ktx.box2d.Query
import ktx.box2d.query
import map.snake.*
import eater.physics.getEntity
import physics.hasObstacle
import eater.physics.isEntity
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
        val maxPoints = (innerBounds.area()).toInt()
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

    companion object {
        const val width = 6
        const val height = 6
        const val tileWidth = 16f
        const val tileHeight = 16f
        const val tileScale = 1 / 4f
        const val scaledWidth = tileWidth * tileScale
        const val scaledHeight = tileHeight * tileScale
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

    private val connectionAlignments by lazy { connections.map { directionAlignment[it]!! }.flatten() }

    val isoTiles by lazy {
        Array(height) { y ->
            Array(width) { x ->
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
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )

                    TileAlignment.BottomLeft -> if (connectionAlignments.contains(TileAlignment.Left) && connectionAlignments.contains(
                            TileAlignment.Bottom
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )

                    TileAlignment.BottomRight -> if (connectionAlignments.contains(TileAlignment.Right) && connectionAlignments.contains(
                            TileAlignment.Bottom
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )

                    TileAlignment.Center -> MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    )

                    TileAlignment.Left -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )

                    TileAlignment.Right -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )

                    TileAlignment.Top -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )

                    TileAlignment.TopLeft -> if (connectionAlignments.contains(TileAlignment.Left) && connectionAlignments.contains(
                            TileAlignment.Top
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )

                    TileAlignment.TopRight -> if (connectionAlignments.contains(TileAlignment.Right) && connectionAlignments.contains(
                            TileAlignment.Top
                        )
                    ) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoFloorTiles.random()))
                        ), true
                    ) else if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.isoWallTiles.random()))
                        ), false
                    )
                }

            }

        }
    }

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