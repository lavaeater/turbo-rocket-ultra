package map.snake

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.graphics.renderables.RenderableTextureRegions
import tru.Assets

fun <T> List<T>.random(): T {
    return this[(0 until this.size).random()]
}

class SnakeMapGenerator {
    fun generate(): SnakeMapManager {

        val size = (10..20).random()
        var previousDirection: MapDirection = MapDirection.North
        var currentDirection: MapDirection = MapDirection.North
        var previousSection = SnakeMapSection(0, 0)
        val snakeMapManager = SnakeMapManager(previousSection)
        for (i in 0..size) {
            if (i != 0) {
                currentDirection =
                    MapDirection.directions.filter { it != MapDirection.opposing[previousDirection] }.random()
            }
            val currentSection = SnakeMapSection(
                previousSection.x + MapDirection.xIndex[currentDirection]!!,
                previousSection.y + MapDirection.yIndex[currentDirection]!!
            )
            currentSection.connections[MapDirection.opposing[currentDirection]!!] = previousSection
            previousSection.connections[currentDirection] = currentSection
            previousSection = currentSection
        }
        return snakeMapManager
    }
}

sealed class MapDirection {
    object North : MapDirection()
    object East : MapDirection()
    object South : MapDirection()
    object West : MapDirection()
    companion object {
        val opposing = mapOf(North to South, South to North, East to West, West to East)
        val directions: List<MapDirection> = listOf(North, East, South, West)
        val xIndex = mapOf(North to 0, South to 0, East to 1, West to -1)
        val yIndex = mapOf(North to 1, South to -1, East to 0, West to 0)
    }
}

sealed class TileAlignment {
    object Center : TileAlignment()
    object Top : TileAlignment()
    object Bottom : TileAlignment()
    object Left : TileAlignment()
    object Right : TileAlignment()
    object TopLeft : TileAlignment()
    object TopRight : TileAlignment()
    object BottomLeft : TileAlignment()
    object BottomRight : TileAlignment()

    companion object {
        val directionAlignment = mapOf(
            MapDirection.North to Top,
            MapDirection.East to Right,
            MapDirection.South to Bottom,
            MapDirection.West to Left
        )
        val alignmentDirection = directionAlignment.entries.associateBy({ it.value }) { it.key }
    }
}

class SnakeMapManager(
    val startSection: SnakeMapSection,
    val tileWidth: Float = 16f,
    val tileHeight: Float = 16f
) {

    var currentSection = startSection
    /*
    We use the worldcenter
    to keep track of which section we are inside. Very useful

    Has to manage transitions between currentSection and, well, any other
    sections. the sections are connected, so that shouldn't be a problem, and we want them seamless.

    Given our actual geography-agnostic map structure, the index of the section actually works nicely - we just have
    to keep track of wether or not the player moves into the "next" tile and then load a new set of tiles
    to currently show when rendering below. Easy peasy.

    Unless we have multiplayer. Fuuuck.

    OK, we'll say we pass into the next section IF the worldCenter is out of bounds!


     */

    val currentSectionX get() = currentSection.x.toFloat() * scale * tileScale
    val currentSectionY get() = currentSection.y.toFloat() * scale * tileScale
    val currentSectionWidth get() = SnakeMapSection.width * tileWidth * scale * tileScale
    val currentSectionHeight get() = SnakeMapSection.height * tileHeight * scale * tileScale

    var bounds = Rectangle(
        currentSectionX,
        currentSectionY,
        currentSectionWidth,
        currentSectionHeight
    )

    fun updateBounds() {
        bounds = Rectangle(
            currentSectionX,
            currentSectionY,
            currentSectionWidth,
            currentSectionHeight
        )
    }

    fun render(batch: Batch, delta: Float, worldCenter: Vector2, scale: Float = 1f) {
        //1. check if we are still inside this section!
        checkAndUpdateBoundsAndSection(worldCenter)
    }

    val tileScale = 4f
    val scale = 1f
    private fun checkAndUpdateBoundsAndSection(worldCenter: Vector2) {
        /*
        1. What are the bounds? I guess it's some kind of tilesize times scale? Ah, we will control
        all tiles, including objects, from here, so we will know the scale, which happens to be 4 at the moment,
        so we'll hardcode that.
         */
        if (bounds.contains(worldCenter))
            return
        //1. Figure out if we are NORTH, EAST, SOUTH or WEST of this currentSection.
        // We should basically only be able to be either one of those things, since we
        // cannot move diagonally
        var direction: MapDirection = if(worldCenter.x < bounds.left()) MapDirection.West
        else if(worldCenter.x > bounds.right()) MapDirection.East
        else if(worldCenter.y < bounds.bottom()) MapDirection.South
        else MapDirection.North

        //Get the section for that particular direction and set that as the new currentDirection,
        // also recalculate bounds
        val newCurrentSection = currentSection.connections[direction]!!

    }
}

fun Rectangle.left(): Float {
    return x
}

fun Rectangle.right(): Float {
    return x + width
}

fun Rectangle.top(): Float {
    return y + height
}

fun Rectangle.bottom(): Float {
    return y
}

class SnakeMapSection(
    val x: Int,
    val y: Int,
    val connections: MutableMap<MapDirection, SnakeMapSection> = mutableMapOf()
) {
    /*
    All sections consist of width x height tiles, and

    the outer rows are all wall, except for the ones where there is a connection, obviously.
     */
    companion object {
        const val width = 32
        const val height = 32
    }

    val connectionAlignments = connections.keys.map { TileAlignment.directionAlignment[it]!! }

    //Tiles can be changed later to add weird features or a class that is a maptile...
    //We should probably have a maptile right now
    val tiles = Array(width * height) {
        val x = it / width
        val y = it % height
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
                )
            ) else MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                )
            )
            TileAlignment.BottomLeft -> MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                )
            )
            TileAlignment.BottomRight -> MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                )
            )
            TileAlignment.Center -> MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                )
            )
            TileAlignment.Left -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                )
            ) else MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                )
            )
            TileAlignment.Right -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                )
            ) else MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                )
            )
            TileAlignment.Top -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                )
            ) else MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallEndTile))
                )
            )
            TileAlignment.TopLeft -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                )
            ) else MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                )
            )
            TileAlignment.TopRight -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                )
            ) else MapTile(
                RenderableTextureRegions(
                    listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                )
            )
        }
    }
}

class MapTile(val renderables: RenderableTextureRegions, val passable: Boolean = true)