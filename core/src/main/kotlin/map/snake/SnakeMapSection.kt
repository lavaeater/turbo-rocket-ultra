package map.snake

import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.gdx.graphics.Color
import ecs.components.graphics.renderables.RenderableTextureRegion
import ecs.components.graphics.renderables.RenderableTextureRegions
import injection.Context.inject
import tru.Assets

class SnakeMapSection(
    val x: Int,
    val y: Int,
    val connections: MutableMap<MapDirection, SnakeMapSection> = mutableMapOf()
) {
    val rayHandler by lazy { inject<RayHandler>() }
    val lights = mutableListOf<PointLight>()
    fun lightsOff() {
        for (light in lights)
            light.isActive = false
    }

    fun lightsOn() {
        //If we have no lights, add some lights!
        if (lights.isEmpty())
            createLights()
        for (light in lights)
            light.isActive = true
    }

    val tileWidth = 16f
    val tileHeight = 16f
    val tileScale = 1 / 2f
    val scale = 1f
    private fun createLights() {
        val lightX = x * width * 16f * tileScale * scale + tileWidth * tileScale * scale
        val lightY = y * height * 16f * tileScale * scale + tileHeight * tileScale * scale
        val pointLight = PointLight(rayHandler, 128, Color(1f, 0f, 1f, 1f), 10f, lightX, lightY)

        rayHandler.setShadows(true)
        pointLight.isStaticLight = false
        pointLight.isSoft = true

        lights.add(pointLight)
    }

    /*
    All sections consist of width x height tiles, and

    the outer rows are all wall, except for the ones where there is a connection, obviously.
     */
    companion object {
        const val width = 8
        const val height = 8
        val directionAlignment by lazy {
            mapOf(
                MapDirection.West to listOf(TileAlignment.Left, TileAlignment.TopLeft),
                MapDirection.North to listOf(TileAlignment.Top),
                MapDirection.East to listOf(TileAlignment.Right, TileAlignment.TopRight),
                MapDirection.South to listOf(TileAlignment.Bottom)
            )
        }
    }


    val connectionAlignments by lazy { connections.keys.map { directionAlignment[it]!! }.flatten() }

    //Tiles can be changed later to add weird features or a class that is a maptile...
    //We should probably have a maptile right now
    val tiles by lazy {
        Array(width) { x ->
            Array(height) { y ->
                val tileAlignment = when (x) {
                    0 -> when (y) {
                        0 -> TileAlignment.TopLeft
                        height - 1 -> TileAlignment.BottomLeft
                        else -> TileAlignment.Left
                    }
                    width - 1 -> when (y) {
                        0 -> TileAlignment.TopRight
                        height - 1 -> TileAlignment.BottomRight
                        else -> TileAlignment.Right
                    }
                    else -> when (y) {
                        0 -> TileAlignment.Top
                        height - 1 -> TileAlignment.Bottom
                        else -> TileAlignment.Center
                    }
                }

                return@Array when (tileAlignment) {
                    TileAlignment.Bottom -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.BottomLeft -> MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.BottomRight -> MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.Center -> MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    )
                    TileAlignment.Left -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.Right -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.Top -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.floorTiles.random()))
                        ), true
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallEndTile))
                        ), false
                    )
                    TileAlignment.TopLeft -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallEndTile))
                        ), false
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                    TileAlignment.TopRight -> if (connectionAlignments.contains(tileAlignment)) MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallEndTile))
                        ), false
                    ) else MapTile(
                        RenderableTextureRegions(
                            listOf(RenderableTextureRegion(Assets.wallTiles.random()))
                        ), false
                    )
                }
            }
        }
    }
}