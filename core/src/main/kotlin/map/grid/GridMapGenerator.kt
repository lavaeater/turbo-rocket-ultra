package map.grid

import box2dLight.Light
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Rectangle
import ecs.components.enemy.EnemySpawnerComponent
import factories.Box2dCategories
import factories.objective
import factories.obstacle
import injection.Context
import injection.Context.inject
import map.snake.MapDirection
import map.snake.random
import map.snake.randomPoint

class GridMapGenerator {
    companion object {
        val engine by lazy { inject<Engine>() }
        val rayHandler by lazy { Context.inject<RayHandler>() }
        fun addObjective(bounds: Rectangle) {
            val position = bounds.randomPoint()
            objective(position.x, position.y)

            val emitter = obstacle(position.x + 10f, position.y + 10f)
            emitter.add(engine.createComponent(EnemySpawnerComponent::class.java))
        }

        fun generate(length: Int, objectiveDensity: Int = 4): Map<Coordinate, GridMapSection> {
            Light.setGlobalContactFilter(
                Box2dCategories.light,
                0, Box2dCategories.allButSensors
            )
            rayHandler.setAmbientLight(.01f)
            rayHandler.setBlurNum(3)

            val width = length
            val height = length
            var map = Array(width) {
                Array(height) {
                    false
                }
            }
            /*
            Where to start? Well, at width / 2 and bottom, which is, what, ymax?
            I have decided, for now, that this is y-down, so 0:maxY is bottom left.
            we start at width / 2: maxy
             */
            var x = width / 2
            var y = map[0].lastIndex
            val maxX = map.lastIndex
            val maxY = y
            map[x][y] = true
            val startCoord = Coordinate(x, y)

            for (index in 0 until length) {
                /*
                Talking about what we prefer we would probably prefer to keep going in the current direction
                to get maps that are on the straighter side.
                 */
                var currentDirection: MapDirection = MapDirection.North
                val directionsToFilter = mutableSetOf<MapDirection>()
                if (index != 0) {
                    directionsToFilter.clear()
                    if (x == 0)
                        directionsToFilter.add(MapDirection.West)
                    if (x == maxX)
                        directionsToFilter.add(MapDirection.East)
                    if (y == 0)
                        directionsToFilter.add(MapDirection.North)
                    if (y == maxY)
                        directionsToFilter.add(MapDirection.South)

                    directionsToFilter.add(MapDirection.opposing[currentDirection]!!)
                    if (directionsToFilter.size == 4) {
                        val waht = "wwwaaat"
                    }

                    currentDirection = MapDirection.directions.filter { !directionsToFilter.contains(it) }.random()
                }
                x += MapDirection.xIndex[currentDirection]!!
                y += MapDirection.yIndex[currentDirection]!!
                if (x < 0)
                    x = 0
                if (x > maxX)
                    x = maxX
                if (y < 0)
                    y = 0
                if (y > maxY)
                    y = maxY

                map[x][y] = true

            }
            /*
            We have filled the glorious array with true or false. Now all we have to do is...

            Yeah, what?

            Iterate over every section. Find the neighbours of the sections. create connections?
            Well, connections aren't really needed in the same way as before, with this variant of map
            we can simply say that certain directions are wall and others not.
             */
            val tileMap = mutableMapOf<Coordinate, GridMapSection>()

            var index = 0
            for ((x, column) in map.withIndex()) {
                for ((y, tile) in column.withIndex())
                    if (tile) {
                        index++
                        //1. Check neighbours - if they are true, we will add them as connections
                        val coordinate = Coordinate(x, y)
                        val section = GridMapSection(coordinate, getConnections(x, y, map), coordinate == startCoord)

                        tileMap[coordinate] = section
                        if (index % objectiveDensity == 0)
                            addObjective(section.innerBounds)
                    }
            }
            return tileMap

        }

        fun getConnections(x: Int, y: Int, map: Array<Array<Boolean>>): Set<MapDirection> {
            val returnSet = mutableSetOf<MapDirection>()
            for (direction in MapDirection.directions) {
                val nX = x + MapDirection.xIndex[direction]!!
                val nY = y + MapDirection.yIndex[direction]!!
                if (nX >= 0 && nX <= map.lastIndex && nY >= 0 && nY <= map.first().lastIndex) {
                    //This tile exists, so we can have a connection to it
                    if (map[nX][nY])
                        returnSet.add(direction)
                }
            }
            return returnSet
        }
    }
}
