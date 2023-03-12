package eater.world

interface Tile {
    val neighbours: MutableList<Tile>
    val x: Int
    val y: Int
}