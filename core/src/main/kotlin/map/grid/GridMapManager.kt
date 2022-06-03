package map.grid

import ai.pathfinding.TileGraph
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import ecs.systems.graphics.GameConstants
import factories.Box2dCategories
import factories.world
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.filter
import map.grid.GridMapSection.Companion.tileHeight
import map.grid.GridMapSection.Companion.tileScale
import map.grid.GridMapSection.Companion.tileWidth
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer

class GridMapManager {
    lateinit var sectionGraph: TileGraph
    var gridMap: Map<Coordinate, GridMapSection> = mapOf()
    set(value) {
        field = value
        fixBodies()
    }

    fun removeLights(oldMap: Map<Coordinate, GridMapSection>) {
        for(section in oldMap.values) {
            for(l in section.lights) {
                l.remove(true)
            }
        }
    }

    private val bodies = mutableListOf<Body>()

    fun getRandomSection(except: Coordinate, minDistance: Int = 2, maxDistance: Int = 5, level: Int = 0) : Coordinate? {
        if(level > GameConstants.MAX_RANDOM_SECTION_RECURSION_LEVEL)
            return null

        val minXa = except.x - maxDistance
        val minXb = except.x - minDistance
        val maxXa = except.x + maxDistance
        val maxXb = except.x + minDistance
        val minYa = except.y - maxDistance
        val minYb = except.y - minDistance
        val maxYa = except.y + maxDistance
        val maxYb = except.y + minDistance
        val allKeys = gridMap
            .keys
            .filter { it != except && it.x > minXa && it.x < maxXa && it.y > minYa && it.y < maxYa }
        val innerKeys = gridMap
            .keys
            .filter { it != except && it.x > minXb && it.x < maxXb && it.y > minYb && it.y < maxYb }
        val outerKeys = allKeys - innerKeys.toSet()
        return if(outerKeys.any()) outerKeys.random() else getRandomSection(except, minDistance, maxDistance + 1, level + 1)
    }

    fun getRandomSection(except: Coordinate, maxDistance: Int = 5): Coordinate {
        val minX = except.x - maxDistance
        val maxX = except.x + maxDistance
        val minY = except.y - maxDistance
        val maxY = except.y + maxDistance
        val keys = gridMap.keys.filter { it != except && it.x > minX && it.x < maxX && it.y > minY && it.y < maxY }
        return if(keys.any()) keys.random() else getRandomSection(except, maxDistance + 1)
    }

    fun canWeBuildAt(x: Int, y:Int) : Boolean {
        return buildableMap.containsKey(x) && buildableMap[x]!!.containsKey(y) && buildableMap[x]!![y]!!
    }

    private fun haveWeVisited(x: Int, y:Int) : Boolean {
        return visitedMap[x]?.get(y) == true
//        return visitedMap.containsKey(x) && visitedMap[x]!!.containsKey(y) && visitedMap[x]!![y]!!
    }

    private val buildableMap = mutableMapOf<Int, MutableMap<Int, Boolean>>()
    private val visitedMap = mutableMapOf<Int, MutableMap<Int, Boolean>>()

    fun visit(sectionX: Int, sectionY: Int) {
        if(!visitedMap.containsKey(sectionX))
            visitedMap[sectionX] = mutableMapOf()
        visitedMap[sectionX]!![sectionY] = true
    }


    fun fixBodies() {
        for(body in bodies)
            world().destroyBody(body)

        bodies.clear()

        for(section in gridMap.values) {
            for ((x, column) in section.tiles.withIndex()) {
                for ((y, tile) in column.withIndex()) {
                    val actualX = section.x * GridMapSection.width + x
                    val actualY = section.y * GridMapSection.height + y
                    if(!buildableMap.containsKey(actualX))
                        buildableMap[actualX] = mutableMapOf()
                    buildableMap[actualX]!![actualY] = tile.passable
                    if(!visitedMap.containsKey(actualX))
                        visitedMap[actualX] = mutableMapOf()
                    visitedMap[actualX]!![actualY] = false

                    if (!tile.passable) {
                        val body = world().body {
                            type = BodyDef.BodyType.StaticBody
                            position.set(
                                x * tileWidth * tileScale - tileWidth * tileScale / 2 + section.x * tileWidth * tileScale * GridMapSection.width,
                                y * tileHeight * tileScale - tileHeight * tileScale / 2 + section.y * tileHeight * tileScale * GridMapSection.height
                            )
                            box(tileWidth * tileScale, tileHeight * tileScale) {
                                filter {
                                    categoryBits = Box2dCategories.walls
                                    maskBits = Box2dCategories.whatWallsHit
                                }
                            }
                        }
                        bodies.add(body)
                    }
                }
            }
        }
    }

    var animationStateTime = 0f
    private val miniMapColor = Color(0.6f, 0.6f, 0.6f, 1f)

    fun renderMiniMap(shapeDrawer: ShapeDrawer, xOffset: Float, yOffset:Float, scale: Float = 1/100f) {
        for (section in gridMap.values) {
            val actualX = section.x
            val actualY = section.y
            if(haveWeVisited(actualX, actualY)) {
                val sectionOffsetX = section.x * section.sectionWidth * scale
                val sectionOffsetY = section.y * section.sectionHeight * scale
                shapeDrawer.filledRectangle(
                    sectionOffsetX + xOffset,
                    sectionOffsetY + yOffset,
                    section.sectionWidth * scale,
                    section.sectionHeight * scale,
                    miniMapColor
                )
            }
            section.lights.forEach { it.isActive = true }
        }
    }

    fun render(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float, scale: Float = 1f) {
        animationStateTime += delta
        for (section in gridMap.values) {
            val sectionOffsetX = section.x * section.sectionWidth * scale
            val sectionOffsetY = section.y * section.sectionHeight * scale
            for ((x, column) in section.tiles.withIndex()) {
                for ((y, tile) in column.withIndex()) {
                    val tileX = x * tileWidth * tileScale * scale
                    val tileY = y * tileHeight * tileScale * scale
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
                }
            }
//            for(point in section.safePoints) {
//                shapeDrawer.rectangle(point.x - 0.5f, point.y - 0.5f, 1f, 1f, Color.GREEN)
//            }
            section.lights.forEach { it.isActive = true }
        }
    }

}