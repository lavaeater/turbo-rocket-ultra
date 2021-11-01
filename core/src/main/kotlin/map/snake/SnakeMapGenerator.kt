package map.snake

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.graphics.renderables.RenderableTextureRegions
import factories.world
import ktx.box2d.body
import ktx.box2d.box
import ktx.math.vec2
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer
import tru.Assets

fun <T> List<T>.random(): T {
    return this[(0 until this.size).random()]
}

class SnakeMapGenerator {
    fun generate(): SnakeMapManager {

        val size = (16..32).random()
        var currentDirection: MapDirection = MapDirection.North
        var previousSection = SnakeMapSection(0, 0)
        val snakeMapManager = SnakeMapManager(previousSection)
        for (i in 0..size) {
            if (i != 0) {
                currentDirection =
                    MapDirection.directions.filter { it != MapDirection.opposing[currentDirection]!! }.random()
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
        val opposing by lazy { mapOf(North to South, South to North, East to West, West to East) }
        val directions: List<MapDirection> by lazy { listOf(North, East, South, West) }
        val xIndex by lazy { mapOf(North to 0, South to 0, East to 1, West to -1) }
        val yIndex by lazy { mapOf(North to -1, South to 1, East to 0, West to 0) }
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

    /*
    Its not enough
     */
}

class SnakeMapManager(
    var currentSection: SnakeMapSection,
    val tileWidth: Float = 16f,
    val tileHeight: Float = 16f
) {
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
    val tileScale = 1 / 2f
    val scale = 1f
    private val currentSectionX get() = currentSection.x * SnakeMapSection.width * tileWidth * tileScale * scale - tileWidth * tileScale * scale
    private val currentSectionY get() = currentSection.y * SnakeMapSection.height * tileHeight * tileScale * scale - tileHeight * tileScale * scale
    private val currentSectionWidth get() = SnakeMapSection.width * tileWidth * scale * tileScale
    private val currentSectionHeight get() = SnakeMapSection.height * tileHeight * scale * tileScale

    var bounds = Rectangle(
        currentSectionX,
        currentSectionY,
        currentSectionWidth,
        currentSectionHeight
    )
    var sectionsToRender: Array<SnakeMapSection> =
        arrayOf(currentSection, *currentSection.connections.values.toTypedArray())

    val bodies = mutableListOf<Body>()

    fun updateCurrentSection(newCurrentSection: SnakeMapSection) {
        currentSection = newCurrentSection
        bounds = Rectangle(
            currentSectionX,
            currentSectionY,
            currentSectionWidth,
            currentSectionHeight
        )

        sectionsToRender = arrayOf(newCurrentSection, *newCurrentSection.connections.values.toTypedArray(), *newCurrentSection.connections.values.flatMap { it.connections.values }.toTypedArray())

        for (body in bodies) {
            world().destroyBody(body)
        }
        bodies.clear()
        for(section in sectionsToRender)
        for ((x, column) in  section.tiles.withIndex()) {
            for ((y, tile) in column.withIndex()) {
                if (!tile.passable) {
                    val body = world().body {
                        type = BodyDef.BodyType.StaticBody
                        position.set(
                            x * tileWidth * tileScale * scale - tileWidth * tileScale * scale / 2 + section.x * tileWidth * tileScale * scale * SnakeMapSection.width,
                            y * tileHeight * tileScale * scale - tileHeight * tileScale * scale / 2 + section.y * tileHeight * tileScale * scale * SnakeMapSection.height
                        )
                        box(tileWidth * tileScale * scale, tileHeight * tileScale * scale) {}
                    }
                    bodies.add(body)
                }
            }
        }
    }

    var firstRun = true
    var animationStateTime = 0f
    fun render(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float, worldCenter: Vector2, scale: Float = 1f) {
        if (firstRun) {
            firstRun = false
            updateCurrentSection(currentSection)
        }

        animationStateTime += delta
        //1. check if we are still inside this section!
        checkAndUpdateBoundsAndSection(worldCenter)
        /*
        We render the current section and then
        we render all sections connected to this one.
         */

        // Render a rectangle on the bounds


        var sectionCount = 0
        for (section in sectionsToRender) {
            sectionCount++
            for ((x, tileArray) in section.tiles.withIndex())
                for ((y, tile) in tileArray.withIndex()) {
                    val sectionOffsetX = section.x * SnakeMapSection.width * tileWidth * tileScale * scale
                    val sectionOffsetY = section.y * SnakeMapSection.height * tileHeight * tileScale * scale
                    val tileX = x * 16f * tileScale * scale
                    val tileY = y * 16f * tileScale * scale
                    val actualX = tileX + sectionOffsetX
                    val actualY = tileY + sectionOffsetY
                    for (region in tile.renderables.regions) {
                        val textureRegion = region.textureRegion
                        batch.drawScaled(
                            textureRegion,
                            actualX,
                            actualY,
                            tileScale * scale
                        )
                    }
//                    val paintColor = when (x) {
//                        0 -> when (y) {
//                            0 -> Color.BLUE//TOPLeft
//                            SnakeMapSection.height - 1 -> Color.RED    // BottomLeft
//                            else -> Color.BLACK
//                        }
//                        SnakeMapSection.width - 1 -> when (y) {
//                            0 -> Color.GREEN//TOPRight
//                            SnakeMapSection.height - 1 -> Color.PURPLE    // BottomRIGHT
//                            else -> Color.BLACK
//                        }
//                        else -> Color.BLACK
//                    }
//                    shapeDrawer.filledCircle(
//                        actualX - tileWidth / 2 * scale * tileScale,
//                        actualY - tileHeight / 2 * scale * tileScale,
//                        1f,
//                        paintColor
//                    )
                }

        }
        /*
        for (direction in currentSection.connections.keys) {
            when (direction) {
                MapDirection.North -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.horizontalCenter(),
                    bounds.top(),
                    tileScale * scale
                )
                MapDirection.East -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.right(),
                    bounds.verticalCenter(),
                    tileScale * scale
                )
                MapDirection.South -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.horizontalCenter(),
                    bounds.bottom(),
                    tileScale * scale
                )
                MapDirection.West -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.left(),
                    bounds.verticalCenter(),
                    tileScale * scale
                )
            }
        }
        shapeDrawer.rectangle(bounds)*/
    }

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
        var direction: MapDirection = if (worldCenter.x < bounds.left()) MapDirection.West
        else if (worldCenter.x > bounds.right()) MapDirection.East
        else if (worldCenter.y < bounds.bottom()) MapDirection.North
        else MapDirection.South

        //Get the section for that particular direction and set that as the new currentDirection,
        // also recalculate bounds
        updateCurrentSection(currentSection.connections[direction]!!)
    }
}

fun Rectangle.left(): Float {
    return x
}

fun Rectangle.right(): Float {
    return x + width
}

fun Rectangle.top(): Float {
    return y
}

fun Rectangle.verticalCenter(): Float {
    return y + height / 2
}

fun Rectangle.horizontalCenter(): Float {
    return x + width / 2
}

fun Rectangle.bottom(): Float {
    return y + height
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
        const val width = 4
        const val height = 4
        val directionAlignment by lazy {  mapOf(
            MapDirection.West to listOf(TileAlignment.Left, TileAlignment.TopLeft),
            MapDirection.North to listOf(TileAlignment.Top),
            MapDirection.East to listOf(TileAlignment.Right, TileAlignment.TopRight),
            MapDirection.South to listOf(TileAlignment.Bottom)
        )}
    }


    val connectionAlignments by lazy { connections.keys.map { directionAlignment[it]!! }.flatten() }

    //Tiles can be changed later to add weird features or a class that is a maptile...
    //We should probably have a maptile right now
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
                    TileAlignment.BottomLeft -> MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.BottomRight -> MapTile(
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
                    TileAlignment.TopLeft -> if ( connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallEndTile))
                        ), false
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false)
                    TileAlignment.TopRight -> if (connectionAlignments.contains(tileAlignment)) MapTile(
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

class MapTile(val renderables: RenderableTextureRegions, val passable: Boolean)