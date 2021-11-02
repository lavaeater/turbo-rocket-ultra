package map.grid

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import factories.world
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.filter
import map.grid.GridMapSection.Companion.tileHeight
import map.grid.GridMapSection.Companion.tileScale
import map.grid.GridMapSection.Companion.tileWidth
import map.snake.SnakeMapSection
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer

class GridMapManager {
    var gridMap: Map<Coordinate, GridMapSection> = mapOf()
    set(value) {
        field = value
        fixBodies()
    }

    val bodies = mutableListOf<Body>()

    fun fixBodies() {
        bodies.clear()
        for(section in gridMap.values) {
            for ((x, column) in section.tiles.withIndex()) {
                for ((y, tile) in column.withIndex()) {
                    if (!tile.passable) {
                        val body = world().body {
                            type = BodyDef.BodyType.StaticBody
                            position.set(
                                x * tileWidth * tileScale - tileWidth * tileScale / 2 + section.x * tileWidth * tileScale * GridMapSection.width,
                                y * tileHeight * tileScale - tileHeight * tileScale / 2 + section.y * tileHeight * tileScale * GridMapSection.height
                            )
                            box(tileWidth * tileScale, tileHeight * tileScale) {}
                        }
                        bodies.add(body)
                    }
                }
            }
        }
    }

    var animationStateTime = 0f
    fun render(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float, scale: Float = 1f) {
        animationStateTime += delta
        for (section in gridMap.values) {
            val sectionOffsetX = section.x * section.sectionWidth * scale
            val sectionOffsetY = section.y * section.sectionHeight * scale
            for ((x, column) in section.tiles.withIndex()) {
                for ((y, tile) in column.withIndex()) {
                    val tileX = x * tileWidth * tileScale * scale
                    val tileY = y * tileHeight * tileScale * scale
                    val actualX = tileX + sectionOffsetX
                    val actualY = tileY + sectionOffsetY
                    for (region in tile.renderables.regions) {
                        val textureRegion = region.textureRegion
                        batch.drawScaled(
                            textureRegion,
                            actualX,
                            actualY,
                            tileScale * scale
                        )
                    }
                }
            }
        }
    }
}