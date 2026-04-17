package ai.behaviorTree.tasks.leaf

import common.injection.InjectionContext.Companion.inject
import injection.Context
import map.grid.Coordinate
import map.grid.GridMapManager

object SectionFindingMethods {
    fun randomOfAll(origin: Coordinate) : Coordinate {
        return inject<GridMapManager>().getRandomSection(origin)
    }

    fun classicRandom(origin: Coordinate, minDistance:Int, maxDistance: Int): Coordinate? {
        //1. Randomly select a section to move to
        return inject<GridMapManager>().getRandomSection(origin, minDistance, maxDistance)
    }
}