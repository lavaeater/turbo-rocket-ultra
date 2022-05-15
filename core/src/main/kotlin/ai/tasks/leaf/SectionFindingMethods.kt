package ai.tasks.leaf

import injection.Context
import map.grid.Coordinate
import map.grid.GridMapManager

object SectionFindingMethods {
    fun classicRandom(origin: Coordinate, maxDistance: Int): Coordinate {
        //1. Randomly select a section to move to
        return Context.inject<GridMapManager>().getRandomSection(origin, maxDistance)
    }
}