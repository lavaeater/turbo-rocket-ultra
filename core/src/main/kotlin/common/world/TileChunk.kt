package common.world

data class TileChunk<T: Tile>(val key: ChunkKey, val tilesPerSide: Int, private val factory: (Int, Int) -> T) {
    constructor(x: Int, y: Int, tilesPerSide: Int, factory: (Int, Int) -> T) : this(ChunkKey(x, y), tilesPerSide, factory)

    val chunkX = key.chunkX
    val chunkY = key.chunkY
    val minX = chunkX * tilesPerSide
    val maxX = minX + tilesPerSide - 1
    val minY = chunkY * tilesPerSide
    val maxY = minY + tilesPerSide - 1
    val tiles = List(tilesPerSide * tilesPerSide) { i ->
        val x = (i % tilesPerSide) + chunkX * tilesPerSide
        val y = (i / tilesPerSide) + chunkY * tilesPerSide
         factory(x, y)
    }
    var neighboursAreFixed = false

    fun localX(worldX: Int): Int {
        return worldX - (tilesPerSide * chunkX)
    }

    fun localY(worldY: Int): Int {
        return worldY - (tilesPerSide * chunkY)
    }

    fun getIndex(localX: Int, localY: Int): Int {
        return localX + tilesPerSide * (localY)
    }

    fun getTileAt(worldX: Int, worldY: Int): T {
        return tiles[getIndex(localX(worldX), localY(worldY))]
    }
}