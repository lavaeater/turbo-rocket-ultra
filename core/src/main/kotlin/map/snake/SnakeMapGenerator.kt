package map.snake

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.graphics.renderables.RenderableTextureRegions
import tru.Assets

fun <T> List<T>.random(): T {
    return this[(0 until this.size).random()]
}

class SnakeMapGenerator {
    fun generate() {

        val size = (10..20).random()
        var previousDirection: MapDirection = MapDirection.North
        var currentDirection: MapDirection = MapDirection.North
        var previousSection = SnakeMapSection()
        val snakeMapManager = SnakeMapManager(previousSection)
        for (i in 0..size) {
            if (i != 0) {
                currentDirection =
                    MapDirection.directions.filter { it != MapDirection.opposing[previousDirection] }.random()
            }
            val currentSection = SnakeMapSection()
            currentSection.connections[MapDirection.opposing[currentDirection]!!] = previousSection
            previousSection.connections[currentDirection] = currentSection
            previousSection = currentSection
        }
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
    }
}

sealed class TileAlignment {
    object Center: TileAlignment()
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
            MapDirection.West to Left)
        val alignmentDirection = directionAlignment.entries.associateBy ({ it.value }) {it.key}
    }
}

class SnakeMapManager(val startSection: SnakeMapSection) {
    /*
    We use the worldcenter
    to keep track of which section we are inside. Very useful

     */


    fun render(batch: Batch, delta: Float, worldCenter: Vector2) {

    }
}

class SnakeMapSection(val connections: MutableMap<MapDirection, SnakeMapSection> = mutableMapOf()) {
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

        return@Array when(tileAlignment) {
            TileAlignment.Bottom -> if(connectionAlignments.contains(tileAlignment)) MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.floorTiles.random())))
            ) else MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallTiles.random())))
            )
            TileAlignment.BottomLeft -> MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallTiles.random())))
            )
            TileAlignment.BottomRight -> MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallTiles.random())))
            )
            TileAlignment.Center -> MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.floorTiles.random())))
            )
            TileAlignment.Left -> if(connectionAlignments.contains(tileAlignment)) MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.floorTiles.random())))
            ) else MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallTiles.random())))
            )
            TileAlignment.Right -> if(connectionAlignments.contains(tileAlignment)) MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.floorTiles.random())))
            ) else MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallTiles.random())))
            )
            TileAlignment.Top -> if(connectionAlignments.contains(tileAlignment)) MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.floorTiles.random())))
            ) else MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallEndTile)))
            )
            TileAlignment.TopLeft -> if(connectionAlignments.contains(tileAlignment)) MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.floorTiles.random())))
            ) else MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallTiles.random())))
            )
            TileAlignment.TopRight -> if(connectionAlignments.contains(tileAlignment)) MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.floorTiles.random())))
            ) else MapTile(RenderableTextureRegions(
                listOf(RenderableTextureRegion(Assets.wallTiles.random())))
            )
        }
    }
}

class MapTile(val renderables: RenderableTextureRegions, val passable: Boolean = true)