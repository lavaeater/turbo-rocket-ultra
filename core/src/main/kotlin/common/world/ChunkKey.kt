package common.world

data class ChunkKey(val chunkX: Int, val chunkY: Int) {
    companion object {
        fun keyForTileCoords(worldX: Int, worldY: Int, tilesPerSide:Int): ChunkKey {
            val chunkX = Math.floorDiv(worldX - tilesPerSide, tilesPerSide) + 1
            val chunkY = Math.floorDiv(worldY - tilesPerSide, tilesPerSide) + 1

            return ChunkKey(chunkX, chunkY)
        }
    }
}