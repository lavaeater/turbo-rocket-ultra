package common.world

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import eater.injection.InjectionContext
import ktx.log.info


fun Body.tileX(tileSize: Float): Int {
    return this.position.tileX(tileSize)
}
fun Body.tileY(tileSize: Float): Int {
    return this.position.tileY(tileSize)
}

fun Vector2.tileX(tileSize: Float): Int {
    return MathUtils.floor(this.x / tileSize)
}

fun Vector2.tileY(tileSize: Float): Int {
    return MathUtils.floor(this.y / tileSize)
}

interface ITileManager {
    /**
     * Returns the chunk that x and y belongs to
     * and all neigbouring chunks
     */
    fun updateCurrentChunks(tileX: Int, tileY: Int)
}

abstract class AbstractTileManager<T: Tile>(private val tilesPerSide: Int) : ITileManager {
    private val chunks = mutableMapOf<ChunkKey, TileChunk<T>>()
    var allTiles: List<T> = chunks.values.flatMap { it.tiles }.toList()
    private var currentWorldX = 5000
    private var currentWorldY = 5000
    private var currentChunkKey = ChunkKey(currentWorldX, currentWorldY)
    private var currentChunks = emptyList<TileChunk<T>>()
    private var currentTiles = emptyList<T>()
    private fun chunkKeyFromTileCoords(x: Int, y: Int): ChunkKey {
        return ChunkKey.keyForTileCoords(x, y, tilesPerSide)
    }

    private fun getOrCreateChunk(key: ChunkKey): TileChunk<T> {
        if (!chunks.containsKey(key)) {
            chunks[key] = createChunk(key)
        }
        return chunks[key]!!
    }

    protected abstract fun createChunk(key: ChunkKey): TileChunk<T>
    fun getCurrentTiles(): List<T> {
        return currentTiles
    }

    fun getCurrentChunks(): List<TileChunk<T>> {
        return currentChunks
    }

    fun areaAhead(tile: T,
    directionX: Int,
    directionY: Int,
    distance: Int,
    width: Int = 3,
    excludeSelf: Boolean = true
    ): List<T> {
        val widthOffset = (width - 1) / 2
        val keys = mutableListOf<Pair<Int, Int>>()
        for (wOff in -widthOffset..widthOffset) {
            for (x in 0..(directionX * distance))
                for (y in 0..(directionY * distance)) {
                    keys.add(Pair(tile.x + x + wOff * directionX, tile.y + y + wOff * directionY))
                }
        }

        val tiles = keys.map { k -> getTileAt(k.first, k.second) }

        return if (excludeSelf) tiles - tile else tiles
    }

    fun areaAround(tile: T, distance: Int = 5, excludeSelf: Boolean = true): List<T> {
        val minX = tile.x - distance
        val maxX = tile.x + distance
        val xRange = minX..maxX
        val minY = tile.y - distance
        val maxY = tile.y + distance
        val yRange = minY..maxY
        val tiles = (xRange).map { x -> (yRange).map { y -> getTileAt(x, y) } }.flatten()

        return if (excludeSelf) tiles - tile else tiles
    }

    fun someTileAt(tile: T, distance: Int, directionX: Int, directionY: Int): T {
        val targetX = tile.x + directionX * distance
        val targetY = tile.y + directionY * distance
        return getTileAt(targetX, targetY)
    }

    fun someAreaAt(tile: T, distance: Int, direction: TileDirection, radius: Int): List<T> {
        val targetX = tile.x + direction.x * distance
        val targetY = tile.y + direction.y * distance

        val minX = targetX - radius
        val minY = targetY - radius
        val maxX = targetX + radius
        val maxY = targetY + radius

        return (minX..maxX).map { x -> (minY..maxY).map { y -> getTileAt(x, y) } }.flatten()
    }

    /**
     * Returns the chunk that x and y belongs to
     * and all neigbouring chunks
     */
    override fun updateCurrentChunks(tileX: Int, tileY: Int) {
        if (tileX != currentWorldX && tileY != currentWorldY) {
            currentWorldX = tileX
            currentWorldY = tileY
            val newChunkKey = chunkKeyFromTileCoords(tileX, tileY)
            if (currentChunkKey != newChunkKey) {
                currentChunkKey = newChunkKey
                val minX = currentChunkKey.chunkX - 2
                val maxX = currentChunkKey.chunkX + 2
                val minY = currentChunkKey.chunkY - 2
                val maxY = currentChunkKey.chunkY + 2
                val keys = (minX..maxX).map { x -> (minY..maxY).map { y -> ChunkKey(x, y) } }.flatten()
                currentChunks = keys.map { getOrCreateChunk(it) }
                currentTiles = currentChunks.map { it.tiles }.flatten()
                allTiles = chunks.values.map { it.tiles }.flatten()
                fixNeighbours()
            }
        }
    }

    private fun hasNeighbours(chunkKey: ChunkKey): Boolean {
        val minX = chunkKey.chunkX - 1
        val maxX = chunkKey.chunkX + 1
        val minY = chunkKey.chunkY - 1
        val maxY = chunkKey.chunkY + 1
        val keys = (minX..maxX).map { x -> (minY..maxY).map { y -> ChunkKey(x, y) } }.flatten() - chunkKey
        return keys.all { chunks.containsKey(it) }
    }

    private fun fixNeighbours() {
        for ((key, chunk) in chunks.filterValues { !it.neighboursAreFixed }) {
            if (hasNeighbours(key)) {
                chunk.neighboursAreFixed = true
                for (x in chunk.minX..chunk.maxX)
                    for (y in chunk.minY..chunk.maxY) {
                        val tile = chunk.getTileAt(x, y)
                        if (tile.neighbours.count() < 8) {
                            tile.neighbours.clear()
                            for (offsetX in -1..1)
                                for (offsetY in -1..1) {
                                    val nX = x + offsetX
                                    val nY = y + offsetY
                                    val nTile = getTileAt(nX, nY)
                                    tile.neighbours.add(nTile)
                                }
                        }
                    }
            }
        }
    }

    fun getTileAt(worldX: Int, worldY: Int): T {
        val chunkKey = chunkKeyFromTileCoords(worldX, worldY)
        return getOrCreateChunk(chunkKey).getTileAt(worldX, worldY)
    }
}