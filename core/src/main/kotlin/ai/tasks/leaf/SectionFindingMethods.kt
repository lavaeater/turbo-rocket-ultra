package ai.tasks.leaf

import eater.injection.InjectionContext.Companion.inject
import injection.Context
import map.grid.Coordinate
import map.grid.GridMapManager

object SectionFindingMethods {
    fun classicRandom(origin: Coordinate, minDistance:Int,  maxDistance: Int): Coordinate? {
        //1. Randomly select a section to move to
        return inject<GridMapManager>().getRandomSection(origin, minDistance, maxDistance)
    }
}