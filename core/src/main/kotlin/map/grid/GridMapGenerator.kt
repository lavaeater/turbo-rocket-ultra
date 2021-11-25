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

class GridMapGenerator {
    companion object {
        val engine by lazy { inject<Engine>() }
        val rayHandler by lazy { inject<RayHandler>() }
        fun addObjective(bounds: Rectangle) {
            var position = bounds.randomPoint()
            objective(position.x, position.y)

            position = bounds.randomPoint()
            val emitter = obstacle(position.x, position.y)
            emitter.add(engine.createComponent(EnemySpawnerComponent::class.java))
        }

        fun addObstacle(bounds: Rectangle) {
            var position = bounds.randomPoint()
            obstacle(position.tileWorldX(), position.tileWorldY())
        }

        fun addBoss(bounds: Rectangle) {
            var position = bounds.randomPoint()
            boss(position, 1)
        }

        lateinit var graph: TileGraph
        fun generateFromDefintion(def: SimpleGridMapDef): Map<Coordinate, GridMapSection> {
            //TODO: Move this somewhere
            Light.setGlobalContactFilter(
                Box2dCategories.lights,
                0, Box2dCategories.allButSensors
            )
            rayHandler.setAmbientLight(.5f)
            rayHandler.setBlurNum(3)

            val tileMap = mutableMapOf<Coordinate, GridMapSection>()
            graph = TileGraph()

            var index = 0
            for ((x, column) in def.booleanSections.withIndex()) {
                for ((y, tile) in column.withIndex())
                    if (tile) {
                        index++
                        //1. Check neighbours - if they are true, we will add them as connections
                        val coordinate = Coordinate(x, y)
                        val connections = getConnections(x, y, def.booleanSections)
                        graph.addCoordinate(coordinate)
                        for (direction in connections) {
                            val connectionCoordinate = Coordinate(
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

                        if (def.hasLoot(coordinate)) {
                            randomLoot(
                                section.innerBounds.randomPoint(),
                                LootTable(
                                    mutableListOf(
                                        *WeaponDefinition.weapons.map { WeaponLoot(it, 5f) }.toTypedArray(),
                                        AmmoLoot(AmmoType.NineMilliMeters, 17..51, 10f),
                                        AmmoLoot(AmmoType.FnP90Ammo, 25..150, 10f),
                                        AmmoLoot(AmmoType.TwelveGaugeShotgun, 4..18, 10f),
                                    ), (3..5).random()
                                )
                            )
                        }
                    }
            }
            return tileMap
        }

        fun generate(length: Int, level: Int): Map<Coordinate, GridMapSection> {
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

            var index = 0
            for ((x, column) in map.withIndex()) {
                for ((y, tile) in column.withIndex())
                    if (tile) {
                        index++
                        //1. Check neighbours - if they are true, we will add them as connections
                        val coordinate = Coordinate(x, y)
                        val section = GridMapSection(coordinate, getConnections(x, y, map), coordinate == startCoord)

                        tileMap[coordinate] = section
                        if (objectives.contains(coordinate)) {
                            addObjective(section.innerBounds)
                        }

                        if ((1..20).random() <= level) {
                            val position = section.innerBounds.randomPoint()
                            val emitter = obstacle(position.x, position.y)
                            emitter.add(engine.createComponent(EnemySpawnerComponent::class.java))
                        }


                        if (coordinate == bossCoordinate) {
                            boss(section.innerBounds.randomPoint(), level)
                        }
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

class SimpleGridMapDef(val def: List<String>) {

    fun hasLoot(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'l'
    }

    fun hasStart(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 's'
    }

    fun hasGoal(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'g'
    }

    fun hasObstacle(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'o'
    }

    fun hasBoss(coordinate: Coordinate): Boolean {
        return sections[coordinate.x][coordinate.y] == 'b'
    }

    val booleanSections
        get() : Array<Array<Boolean>> {
            return sections.map { column -> column.toCharArray().map { it != 'e' }.toTypedArray() }.toTypedArray()
        }

    val sections
        get(): Array<Array<Char>> {
            val mapWidth = def.map { it.length }.maxOf { it }
            val mapHeight = def.size
            val s = Array(mapWidth) { x ->
                Array(mapHeight) { y ->
                    def[y][x]
                }
            }
            return s
        }

    companion object {
        val levelOne = SimpleGridMapDef(
            """
            xxxxgxxxb
            xeeeexeee
            xeeeexxxl
            xxlxxxeex
            xxxeelxxx
            xxeeeeeex
            sxxxxxxxx
        """.trimIndent().lines()
        )
        val levelTwo = SimpleGridMapDef(
            """
                xxxxxxxxxxxxxxxxxxxxxx
                xeeeeeeeeeeeeeeeeeeeex
                xexxxxxxxxoxxxxxxxxxxx
                xexeeeeeeeeeeeeeeeeeee
                xexexxxxxxxxxxxxxxxxxx
                xexexeeeeeeeeeeeeeeeex
                xexexexxxxxxxxxxxxxxxx
                xexexexeeeeeeeeeeeeeee
                xexexexxexxxxxxxxxxxxe
                xexxxegbexboxeeeeeeexe
                xeeeeeeeexooxexxxxoexe
                xxxxxxxxxxooxeoeexxexe
                eoeleoelexxxxxxeebgexe
                eeeeeeeeeeeeeeeeeeeexe
                eexxxxxxxxxxxxxxxxxxxe
                exxeeeeeeeeeeeeeeeeeee
                exeeeeeeeeeeexooeeeeee
                exxxxxxxxxxxxlooeeeeee
                exeeeeeeeeeeexxxeeeeee
                sxeeeeeeeeeeeeeeeeeeee
            """.trimIndent().lines()
        )
    }
}



