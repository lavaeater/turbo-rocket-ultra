package map.snake

import box2dLight.Light
import box2dLight.RayHandler
import factories.Box2dCategories
import injection.Context.inject

class SnakeMapGenerator {
    companion object {
        val rayHandler by lazy { inject<RayHandler>() }
        fun generate(minMax: IntRange = 16..32): SnakeMapSection {
            Light.setGlobalContactFilter(Box2dCategories.light,
                0, Box2dCategories.allButSensors)
            rayHandler.setAmbientLight(.5f)
            rayHandler.setBlurNum(3)
            val size = minMax.random()
            var currentDirection: MapDirection = MapDirection.North
            var previousSection = SnakeMapSection(0, 0)
            val returnSection = previousSection
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
            return returnSection
        }
    }
}


