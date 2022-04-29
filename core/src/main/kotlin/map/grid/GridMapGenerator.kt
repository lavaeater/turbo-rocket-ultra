package map.grid

import ai.pathfinding.TileGraph
import box2dLight.Light
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Rectangle
import ecs.components.enemy.EnemySpawnerComponent
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import factories.*
import features.pickups.AmmoLoot
import features.pickups.LootTable
import features.pickups.WeaponLoot
import features.weapons.AmmoType
import features.weapons.WeaponDefinition
import injection.Context.inject
import map.snake.MapDirection
import map.snake.random
import map.snake.randomPoint
import org.w3c.dom.css.Counter
import screens.CounterObject

class GridMapGenerator {
    companion object {
        val engine by lazy { inject<Engine>() }
        val rayHandler by lazy { inject<RayHandler>() }
        fun addObjective(bounds: Rectangle) {
            var position = bounds.randomPoint()
            objective(position.x, position.y)

            position = bounds.randomPoint()
            val emitter = spawner(position.x, position.y)
            emitter.add(engine.createComponent(EnemySpawnerComponent::class.java))
        }

        fun addObstacle(bounds: Rectangle) {
            var position = bounds.randomPoint()
            spawner(position.tileWorldX(), position.tileWorldY())
        }

        fun addBoss(bounds: Rectangle) {
            var position = bounds.randomPoint()
            boss(position, 1)
        }

        fun generateFromDefintion(def: TextGridMapDefinition): Pair<Map<Coordinate, GridMapSection>, TileGraph> {
            //TODO: Move this somewhere
            Light.setGlobalContactFilter(
                Box2dCategories.lights,
                0, Box2dCategories.allButSensors
            )
            rayHandler.setAmbientLight(.5f)
            rayHandler.setBlurNum(3)

            val tileMap = mutableMapOf<Coordinate, GridMapSection>()
            val graph = TileGraph()

            var index = 0
            for ((x, column) in def.booleanSections.withIndex()) {
                for ((y, aSectionIsHere) in column.withIndex())
                    if (aSectionIsHere) {
                        index++
                        //1. Check neighbours - if they are true, we will add them as connections
                        val coordinate = TileGraph.getCoordinateInstance(x, y)
                        val connections = getConnections(x, y, def.booleanSections)
                        //graph.addCoordinate(coordinate)
                        for (direction in connections) {
                            val connectionCoordinate = TileGraph.getCoordinateInstance(
                                coordinate.x + MapDirection.xIndex[direction]!!,
                                coordinate.y + MapDirection.yIndex[direction]!!
                            )

                            graph.connectCoordinates(coordinate, connectionCoordinate)
                        }

                        val section = GridMapSection(
                            coordinate,
                            connections,
                            def.hasStart(coordinate)
                        )

                        tileMap[coordinate] = section
                        if (def.hasGoal(coordinate)) {
                            addObjective(section.innerBounds)
                        }
                        if (def.hasObstacle(coordinate))
                            addObstacle(section.innerBounds)
                        if (def.hasBoss(coordinate))
                            addBoss(section.innerBounds)

                        if(def.hasHackingStation(coordinate))
                            addHackingStation(section.innerBounds, CounterObject.currentLevel)

                        if (def.hasLoot(coordinate)) {
                            randomLoot(
                                section.innerBounds.randomPoint(),
                                LootTable(
                                    mutableListOf(
                                        *WeaponDefinition.weapons.map { WeaponLoot(it, 5f) }.toTypedArray(),
                                        AmmoLoot(AmmoType.NineMilliMeters, 17..51, 10f),
                                        AmmoLoot(AmmoType.FnP90Ammo, 25..150, 10f),
                                        AmmoLoot(AmmoType.TwelveGaugeShotgun, 4..18, 10f),
                                        AmmoLoot(AmmoType.Molotov, 4..18, 10f),
                                    ), (3..5).random()
                                )
                            )
                        }
                    }
            }
            return Pair(tileMap, graph)
        }

        private fun addHackingStation(bounds: Rectangle, level: Int) {
            var position = bounds.randomPoint()
            hackingStation(position, level)
        }

        fun generate(length: Int, level: Int): Pair<Map<Coordinate, GridMapSection>, TileGraph> {
            //TODO: Move this somewhere
            Light.setGlobalContactFilter(
                Box2dCategories.lights,
                0, Box2dCategories.allButSensors
            )
            rayHandler.setAmbientLight(.5f)
            rayHandler.setBlurNum(3)

            val width = length * 3
            val height = length * 3
            val map = Array(width) {
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

            var numberOfObjectivesLeftToDealOut = level * 2 - 1
            val objectives = mutableListOf<Coordinate>()
            lateinit var bossCoordinate: Coordinate

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

                if (numberOfObjectivesLeftToDealOut > 1 && index < (length - 4) && (0..9).random() == 0) {
                    numberOfObjectivesLeftToDealOut--
                    objectives.add(Coordinate(x, y))
                } else if (index > (length - 4) && numberOfObjectivesLeftToDealOut > 0) {
                    numberOfObjectivesLeftToDealOut--
                    objectives.add(Coordinate(x, y))
                    bossCoordinate = Coordinate(x, y)
                }

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
            val graph = TileGraph()
            var index = 0
            for ((sectionX, column) in map.withIndex()) {
                for ((sectionY, tile) in column.withIndex())
                    if (tile) {
                        index++
                        //1. Check neighbours - if they are true, we will add them as connections
                        val coordinate = TileGraph.getCoordinateInstance(sectionX, sectionY)
                        val connections = getConnections(sectionX, sectionY, map)
                        val section = GridMapSection(coordinate, connections, coordinate == startCoord)
                        for (direction in connections) {
                            val connectionCoordinate = TileGraph.getCoordinateInstance(
                                coordinate.x + MapDirection.xIndex[direction]!!,
                                coordinate.y + MapDirection.yIndex[direction]!!
                            )
                            graph.connectCoordinates(coordinate, connectionCoordinate)
                        }
                        tileMap[coordinate] = section
                        if (objectives.contains(coordinate)) {
                            addObjective(section.innerBounds)
                        }

                        if ((1..20).random() <= level) {
                            val position = section.innerBounds.randomPoint()
                            spawner(position.x, position.y)
                        }

                        if (coordinate == bossCoordinate) {
                            boss(section.innerBounds.randomPoint(), level)
                        }
                    }
            }
            return Pair(tileMap,graph)

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



