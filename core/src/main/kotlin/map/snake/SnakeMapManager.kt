package map.snake

import box2dLight.PointLight
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import factories.world
import ktx.box2d.body
import ktx.box2d.box
import physics.drawScaled
import space.earlygrey.shapedrawer.ShapeDrawer

class SnakeMapManager(
    val tileWidth: Float = 16f,
    val tileHeight: Float = 16f,
    val tileScale: Float = 1/2f,
    val scale: Float = 1f
) {
    /*
    We use the worldcenter
    to keep track of which section we are inside. Very useful

    Has to manage transitions between currentSection and, well, any other
    sections. the sections are connected, so that shouldn't be a problem, and we want them seamless.

    Given our actual geography-agnostic map structure, the index of the section actually works nicely - we just have
    to keep track of wether or not the player moves into the "next" tile and then load a new set of tiles
    to currently show when rendering below. Easy peasy.

    Unless we have multiplayer. Fuuuck.

    OK, we'll say we pass into the next section IF the worldCenter is out of bounds!


     */

    var currentSection: SnakeMapSection = SnakeMapSection(0, 0)
        get() = field
            set(value) {
                field = value
                updateCurrentSection()
            }
    private val currentSectionX get() = currentSection.x * SnakeMapSection.width * tileWidth * tileScale * scale - tileWidth * tileScale * scale
    private val currentSectionY get() = currentSection.y * SnakeMapSection.height * tileHeight * tileScale * scale - tileHeight * tileScale * scale
    private val currentSectionWidth get() = SnakeMapSection.width * tileWidth * scale * tileScale
    private val currentSectionHeight get() = SnakeMapSection.height * tileHeight * scale * tileScale

    var bounds = Rectangle(
        currentSectionX,
        currentSectionY,
        currentSectionWidth,
        currentSectionHeight
    )
    var sectionsToRender: Array<SnakeMapSection> =
        arrayOf(currentSection, *currentSection.connections.values.toTypedArray())

    val bodies = mutableListOf<Body>()

    private fun updateCurrentSection() {
        bounds = Rectangle(
            currentSectionX,
            currentSectionY,
            currentSectionWidth,
            currentSectionHeight
        )
        for(section in sectionsToRender)
            section.lightsOff()

        sectionsToRender = arrayOf(currentSection, *currentSection.connections.values.toTypedArray(), *currentSection.connections.values.flatMap { it.connections.values }.toTypedArray())

        for (body in bodies) {
            world().destroyBody(body)
        }
        bodies.clear()

        for(section in sectionsToRender) {
            section.lightsOn()
//            for(light in section.lights)
//                bodies.add(world().body {
//                    type = BodyDef.BodyType.StaticBody
//                    position.set(
//                        light.x,
//                        light.y
//                    )
//                    box(tileWidth * tileScale * scale, tileHeight * tileScale * scale) {}
//                })
            for ((x, column) in section.tiles.withIndex()) {
                for ((y, tile) in column.withIndex()) {
                    if (!tile.passable) {
                        val body = world().body {
                            type = BodyDef.BodyType.StaticBody
                            position.set(
                                x * tileWidth * tileScale * scale - tileWidth * tileScale * scale / 2 + section.x * tileWidth * tileScale * scale * SnakeMapSection.width,
                                y * tileHeight * tileScale * scale - tileHeight * tileScale * scale / 2 + section.y * tileHeight * tileScale * scale * SnakeMapSection.height
                            )
                            box(tileWidth * tileScale * scale, tileHeight * tileScale * scale) {}
                        }
                        bodies.add(body)
                    }
                }
            }
        }
    }

    var firstRun = true
    var animationStateTime = 0f
    fun render(batch: Batch, shapeDrawer: ShapeDrawer, delta: Float, worldCenter: Vector2, scale: Float = 1f) {
        if (firstRun) {
            firstRun = false
            updateCurrentSection()
        }

        animationStateTime += delta
        //1. check if we are still inside this section!
        checkAndUpdateBoundsAndSection(worldCenter)
        /*
        We render the current section and then
        we render all sections connected to this one.
         */

        // Render a rectangle on the bounds


        var sectionCount = 0
        for (section in sectionsToRender) {
            sectionCount++
            for ((x, tileArray) in section.tiles.withIndex())
                for ((y, tile) in tileArray.withIndex()) {
                    val sectionOffsetX = section.x * SnakeMapSection.width * tileWidth * tileScale * scale
                    val sectionOffsetY = section.y * SnakeMapSection.height * tileHeight * tileScale * scale
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
//                    val paintColor = when (x) {
//                        0 -> when (y) {
//                            0 -> Color.BLUE//TOPLeft
//                            SnakeMapSection.height - 1 -> Color.RED    // BottomLeft
//                            else -> Color.BLACK
//                        }
//                        SnakeMapSection.width - 1 -> when (y) {
//                            0 -> Color.GREEN//TOPRight
//                            SnakeMapSection.height - 1 -> Color.PURPLE    // BottomRIGHT
//                            else -> Color.BLACK
//                        }
//                        else -> Color.BLACK
//                    }
//                    shapeDrawer.filledCircle(
//                        actualX - tileWidth / 2 * scale * tileScale,
//                        actualY - tileHeight / 2 * scale * tileScale,
//                        1f,
//                        paintColor
//                    )
                }
            for (light in section.lights)
                shapeDrawer.filledCircle(light.x,light.y, 1f, Color.YELLOW)
        }
        /*
        for (direction in currentSection.connections.keys) {
            when (direction) {
                MapDirection.North -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.horizontalCenter(),
                    bounds.top(),
                    tileScale * scale
                )
                MapDirection.East -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.right(),
                    bounds.verticalCenter(),
                    tileScale * scale
                )
                MapDirection.South -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.horizontalCenter(),
                    bounds.bottom(),
                    tileScale * scale
                )
                MapDirection.West -> batch.drawScaled(
                    Assets.arrows[direction]!!,
                    bounds.left(),
                    bounds.verticalCenter(),
                    tileScale * scale
                )
            }
        }
        shapeDrawer.rectangle(bounds)*/
    }

    private fun checkAndUpdateBoundsAndSection(worldCenter: Vector2) {
        /*
        1. What are the bounds? I guess it's some kind of tilesize times scale? Ah, we will control
        all tiles, including objects, from here, so we will know the scale, which happens to be 4 at the moment,
        so we'll hardcode that.
         */
        if (bounds.contains(worldCenter))
            return
        //1. Figure out if we are NORTH, EAST, SOUTH or WEST of this currentSection.
        // We should basically only be able to be either one of those things, since we
        // cannot move diagonally
        var direction: MapDirection = if (worldCenter.x < bounds.left()) MapDirection.West
        else if (worldCenter.x > bounds.right()) MapDirection.East
        else if (worldCenter.y < bounds.bottom()) MapDirection.North
        else MapDirection.South

        //Get the section for that particular direction and set that as the new currentDirection,
        // also recalculate bounds
        currentSection = currentSection.connections[direction]!!
    }
}