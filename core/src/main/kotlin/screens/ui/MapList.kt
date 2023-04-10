package screens.ui

import map.grid.MapData
import map.grid.MapLoader

object MapList {
    val mapFileNames = mutableListOf<String>()
    val mapFiles: List<MapData>
        get() {
            return mapFileNames.map { MapLoader.loadNewMap("text_maps/$it.txt") }
        }
}