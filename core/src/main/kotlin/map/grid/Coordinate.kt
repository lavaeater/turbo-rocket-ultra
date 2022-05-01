package map.grid

data class Coordinate(var x: Int, var y: Int) {
    var index: Int = 0
    fun set(x: Int, y:Int): Coordinate {
        this.x = x
        this.y = y
        return this
    }
}