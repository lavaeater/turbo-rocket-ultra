package screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import data.selectedItemListOf
import gamestate.GameEvent
import gamestate.GameState
import isometric.toCartesian
import isometric.toIsometric
import ktx.collections.toGdxArray
import ktx.graphics.use
import ktx.math.*
import screens.stuff.AnimatedSpriteNode
import screens.stuff.AnimatedSpriteNode3d
import screens.stuff.Node
import screens.stuff.Node3d
import screens.ui.KeyPress
import statemachine.StateMachine
import tru.AnimState
import tru.Assets
import tru.CardinalDirection
import tru.LpcCharacterAnim

class ConceptScreen(gameState: StateMachine<GameState, GameEvent>) : BasicScreen(gameState) {

    /**
     * Idle : 0-7
     * Shoot: 8-10
     * Run: 11-18
     * Reload: 19-31
     * Death: 32-43
     */
    override val viewport = ExtendViewport(16f, 12f)
    val head by lazy { Texture(Gdx.files.internal("sprites/layered/head.png")) }
    val headTop by lazy { Texture(Gdx.files.internal("sprites/layered/head_top.png")) }
    val eye by lazy { Texture(Gdx.files.internal("sprites/layered/eye.png")) }


//    val nodeTree = Node().apply {
//        addChild(Node().apply {
//            color = Color.YELLOW
//            position = ImmutableVector2(10f, 10f)
//            addChild(AnimatedSpriteNode(head).apply {
//                updateAction = getSmoothUpdateAction(this, true, 1f, vec2(5f, 0f))
//                position = ImmutableVector2(30f, -20f)
//                color = Color.ORANGE
//                rotateWithParent = false
//                addChild(AnimatedSpriteNode(eye).apply {
//                    rotateWithParent = false
//                    position = vec2(7.5f, 0f).toCartesian().toImmutable()
//                })
//                addChild(AnimatedSpriteNode(eye).apply {
//                    rotateWithParent = false
//                    position = vec2(-2.5f, 0f).toCartesian().toImmutable()
//                })
//            })
//        })
//    }

    val spriteNodeTree = Node3d().apply {
        addChild(Node3d(vec3(0F, 10f, 0f), color = Color.RED).apply {
            addChild(Node3d(vec3(0f, 0f, 10f), color = Color.ORANGE))
            addChild(Node3d(vec3(10f, 0f, 0f), color = Color.YELLOW))
        })
    }

//    val node3dTreespriteNodeTree = Node3d().apply {
//        addChild(AnimatedSpriteNode3d(head, vec3(0f, 2f, 0f)).apply {
//            addChild(AnimatedSpriteNode3d(eye, vec3(5f, 0f, 0f), color = Color.GREEN).apply {
//                updateAction = getSmoothUpdateAction3d(this, true, 0.5f, vec3(5f, 0f, 0f))
//            })
//            addChild(AnimatedSpriteNode3d(eye, vec3(-5f, 0f, 0f), color = Color.BLUE))
//        })
//    }

    private fun getSmoothUpdateAction(
        forNode: Node,
        bounce: Boolean = true,
        time: Float = 1f,
        modifier: Vector2 = vec2(15f, 0f)
    ): (Node, Float) -> Unit {
        val basePosition = forNode.position
        var elapsedTime = 0f
        val modVector = vec2()
        val minVector = vec2(-(modifier.x / 2f), -(modifier.y / 2f))
        val maxVector = vec2(modifier.x / 2f, modifier.y / 2f)
        return { node, delta ->
            elapsedTime += delta
            if (elapsedTime > time) {
                elapsedTime = 0f
            }
            val currentFraction = MathUtils.norm(0f, time, elapsedTime)
            if (bounce) {
                val forward = (elapsedTime - time / 2f) < 0f
                if (forward) {
                    modVector.x = MathUtils.lerp(
                        minVector.x,
                        maxVector.x, currentFraction
                    )
                    modVector.y = MathUtils.lerp(
                        minVector.y,
                        maxVector.y, currentFraction
                    )
                } else {
                    modVector.x = MathUtils.lerp(
                        maxVector.x,
                        minVector.x, currentFraction
                    )
                    modVector.y = MathUtils.lerp(
                        maxVector.y,
                        minVector.y, currentFraction
                    )
                }
            } else {
                modVector.x = MathUtils.lerp(
                    minVector.x,
                    maxVector.x, currentFraction
                )
                modVector.y = MathUtils.lerp(
                    minVector.y,
                    maxVector.y, currentFraction
                )
            }
            node.position = basePosition + modVector.toImmutable()
        }
    }

    private fun getSmoothUpdateAction3d(
        forNode: Node3d,
        bounce: Boolean = true,
        time: Float = 1f,
        modifier: Vector3 = vec3(0f, 0f, 15f)
    ): (Node3d, Float) -> Unit {
        val basePosition = forNode.localPosition3d
        var elapsedTime = 0f
        val modVector = vec3()
        val minVector = vec3(-(modifier.x / 2f), -(modifier.y / 2f), -(modifier.z / 2f))
        val maxVector = vec3(modifier.x / 2f, modifier.y / 2f, modifier.z / 2f)
        return { node, delta ->
            elapsedTime += delta
            if (elapsedTime > time) {
                elapsedTime = 0f
            }
            val currentFraction = MathUtils.norm(0f, time, elapsedTime)
            if (bounce) {
                val forward = (elapsedTime - time / 2f) < 0f
                if (forward) {
                    modVector.x = MathUtils.lerp(
                        minVector.x,
                        maxVector.x, currentFraction
                    )
                    modVector.y = MathUtils.lerp(
                        minVector.y,
                        maxVector.y, currentFraction
                    )
                    modVector.z = MathUtils.lerp(
                        minVector.z,
                        maxVector.z, currentFraction
                    )
                } else {
                    modVector.x = MathUtils.lerp(
                        maxVector.x,
                        minVector.x, currentFraction
                    )
                    modVector.y = MathUtils.lerp(
                        maxVector.y,
                        minVector.y, currentFraction
                    )
                    modVector.z = MathUtils.lerp(
                        maxVector.z,
                        minVector.z, currentFraction
                    )
                }
            } else {
                modVector.x = MathUtils.lerp(
                    minVector.x,
                    maxVector.x, currentFraction
                )
                modVector.y = MathUtils.lerp(
                    minVector.y,
                    maxVector.y, currentFraction
                )
                modVector.z = MathUtils.lerp(
                    minVector.z,
                    maxVector.z, currentFraction
                )
            }
            node.localPosition3d = basePosition + modVector
        }
    }

    private fun getSteppedUpdateAction(
        forNode: Node,
        bounce: Boolean = true,
        steps: Int = 6,
        modifier: ClosedFloatingPointRange<Float> = -15f..15f
    ): (Node, Float) -> Unit {
        val basePosition = forNode.position
        var elapsedTime = 0f
        var previousTime = 0f
        val timeStep = 1f / steps
        var currentStep = 0
        return { node, delta ->
            elapsedTime += delta
            val diff = elapsedTime - previousTime
            if (diff > timeStep) {
                previousTime = elapsedTime
                if (currentStep >= steps) {
                    currentStep = 0
                }
                val forward = currentStep < steps / 2 - 1

                val fraction = MathUtils.norm(0f, steps.toFloat() - 1f, currentStep.toFloat())

                val modValue = if (bounce) {
                    if (forward) MathUtils.lerp(
                        modifier.start,
                        modifier.endInclusive, fraction
                    ) else MathUtils.lerp(
                        modifier.endInclusive,
                        modifier.start, fraction
                    )
                } else
                    MathUtils.lerp(
                        modifier.start,
                        modifier.endInclusive, fraction
                    )

                node.position = ImmutableVector2(basePosition.x + modValue, basePosition.y + modValue)
                currentStep++
            }
        }
    }


    val offsetX = 0
    val padX = 0
    val padY = 0
    val offsetY = 0
    val scoutHeight = 150
    val scoutWidth = 150
    val scoutTextures = mapOf(
        CardinalDirection.East to Texture(Gdx.files.internal("sprites/scout/scout_e_small.png")),
        CardinalDirection.NorthEast to Texture(Gdx.files.internal("sprites/scout/scout_ne_small.png")),
        CardinalDirection.North to Texture(Gdx.files.internal("sprites/scout/scout_n_small.png")),
        CardinalDirection.SouthEast to Texture(Gdx.files.internal("sprites/scout/scout_se_small.png")),
        CardinalDirection.South to Texture(Gdx.files.internal("sprites/scout/scout_s_small.png")),
        CardinalDirection.SouthWest to Texture(Gdx.files.internal("sprites/scout/scout_sw_small.png")),
    )

    val animStates = mapOf(
        AnimState.Idle to Pair(0, 8),
        AnimState.Shoot to Pair(1, 3),
        AnimState.Walk to Pair(2, 8),
        AnimState.Death to Pair(4, 11)
    )

    val scoutAnims: Map<AnimState, LpcCharacterAnim<TextureRegion>> = animStates.map { state ->
        state.key to LpcCharacterAnim(
            state.key, CardinalDirection.scoutDirections.map { direction ->
                direction to Animation(1f / 8f, Array(state.value.second) { frame ->
                    TextureRegion(
                        scoutTextures[direction]!!,
                        frame * scoutWidth,
                        state.value.first * scoutHeight,
                        scoutWidth,
                        scoutHeight
                    )
                }.toGdxArray(), Animation.PlayMode.LOOP)
            }.toMap()
        )
    }.toMap()

    val anims = selectedItemListOf(AnimState.Idle, AnimState.Walk, AnimState.Shoot)
    val directions = selectedItemListOf(*CardinalDirection.scoutDirections.toTypedArray())


    /**
     * What we want to do is basically what we did for anchor points, but perhaps
     * with some tweaking?
     *
     * We want two points on the end of a line. They are, like everything else, always
     * projected into an isometric space.
     *
     * So, the points are equidistant from a center point, which is a point, 100,100
     */

    val centerPoint = vec2(0f, 0f)
    val pointsCloud = PointsCloud().apply {
        points.add(vec2(0f, 50f))
        points.add(vec2(50f, 0f))
    }
//    val line = Line(vec2(12.5f, 0f), vec2(-12.5f, 0f))

    /**
     * Now I think I have figured out another thing that I kind of want.
     *
     * The arms need not change length or direction (useful in and of itself, of course),
     * but rather I want to say that attached to a line's endpoint is the startpoint
     * of another line, relating to the first's geometry somehow, perhaps using a
     * related angle or something. A rotation. A hierarchy of points that describe
     * lines that relate to each other.
     *
     * We are truly and deeply into the weeds, but push on, my friend.
     */

//    val baseGeometry: ContainerGeometry by lazy {
//        val shoulderLine = GeometryLine(vec2(), 15f, 45f)
//        val rightArmLine = GeometryLine(vec2(50f, 50f), 45f, 45f)
//        val cGeom = ContainerGeometry(vec2(), 0f).apply {
//            add(shoulderLine)
//            add(rightArmLine)
//        }
//        cGeom
//    }
    var zoom = 0f
    var rotation = 0f
    var extension = 0f
//    val triangle = Triangle(vec2(), 7.5f, 15f, 15f, 165f)


    private val normalCommandMap = command("Normal") {
        setBoth(Input.Keys.Z, "Zoom in", { zoom = 0f }, { zoom = 0.1f })
        setBoth(Input.Keys.X, "Zoom out", { zoom = 0f }, { zoom = -0.1f })
        setBoth(Input.Keys.A, "Rotate Left", { rotation = 0f }) { rotation = 10f }
        setBoth(Input.Keys.D, "Rotate Right", { rotation = 0f }) { rotation = -10f }
        setBoth(Input.Keys.W, "Extend", { extension = 0f }) { extension = .1f }
        setBoth(Input.Keys.S, "Reverse", { extension = 0f }) { extension = -.1f }
        setUp(Input.Keys.LEFT, "Previous State") { anims.previousItem() }
        setUp(Input.Keys.RIGHT, "Next State") { anims.nextItem() }
        setUp(Input.Keys.UP, "Previous State") { directions.previousItem() }
        setUp(Input.Keys.DOWN, "Next State") { directions.nextItem() }
    }
    private val shapeDrawer by lazy { Assets.shapeDrawer }

    override fun keyUp(keycode: Int): Boolean {
        return normalCommandMap.execute(keycode, KeyPress.Up)
    }

    override fun keyDown(keycode: Int): Boolean {
        return normalCommandMap.execute(keycode, KeyPress.Down)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Buttons.LEFT) {
            pointsCloud.addPoint(mousePosition.cpy())
        }
        return true
    }

    val screenMouse = vec3()
    val mousePosition = vec2()
    val mouseToCenter = vec2()
    fun updateMouse() {
        screenMouse.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(screenMouse)
        mousePosition.set(screenMouse.x, screenMouse.y)
        mouseToCenter.set(mousePosition - pointsCloud.position)
//        baseGeometry.worldRotation = mouseToCenter.angleDeg() - 90f
//        line.rotation = mouseToCenter.angleDeg() - 135f
//        triangle.updateInverseKinematic(mousePosition)
        //triangle.rotation = mouseToCenter.angleDeg()
    }

    var elapsedTime = 0f
    val scoutPosition = vec2()
    override fun render(delta: Float) {
        elapsedTime += delta
        updateMouse()
//        baseGeometry.updateGeometry()
//        triangle.updateA(triangle.a + extension)
//        triangle.position.set(line.e1.toMutable())
        camera.position.x = 0f
        camera.position.y = 0f
        camera.zoom = camera.zoom + 0.05f * zoom
//        line.rotation = line.rotation + rotation
        super.render(delta)
        batch.use {
//            shapeDrawer.line(line.e1.toMutable(), line.e2.toMutable(), 1f)
//            shapeDrawer.filledCircle(line.e1.toMutable(), 2.5f, Color.GREEN)
//            shapeDrawer.filledCircle(line.e2.toMutable(), 2.5f, Color.BLUE)
//            shapeDrawer.filledCircle(line.center.toMutable(), 1.5f, Color.RED)


//            baseGeometry.draw(shapeDrawer)
//
//            pointsCloud.worldX = MathUtils.lerp(pointsCloud.worldX, mousePosition.x, 0.01f)
//            pointsCloud.worldY = MathUtils.lerp(pointsCloud.worldY, mousePosition.y, 0.01f)
//            pointsCloud.rotation = pointsCloud.rotation + rotation
            // scoutPosition.set(pointsCloud.worldX - scoutWidth / 2f, pointsCloud.worldY - scoutHeight / 2f)
//            batch.draw(
//                scoutAnims[anims.selectedItem]!!.animations[directions.selectedItem]!!.getKeyFrame(elapsedTime),
//                scoutPosition.x,
//                scoutPosition.y
//            )
//            shapeDrawer.filledCircle(scoutPosition, 1f, Color.GREEN)
//            scoutPosition.set(pointsCloud.worldX + scoutWidth / 2, pointsCloud.worldY + scoutHeight / 2)
//            shapeDrawer.filledCircle(scoutPosition, 1f, Color.YELLOW)
//            shapeDrawer.filledCircle(pointsCloud.position, 1f, Color.ORANGE)
//            for (point in pointsCloud.actualPoints) {
//                shapeDrawer.filledCircle(point, 1f, Color.RED)
//            }
//            nodeTree.rotation = nodeTree.rotation + rotation
//            nodeTree.update(delta)
//            nodeTree.drawIso(batch, shapeDrawer, delta)
//            node3dTree.update(delta)
            //node3dTree.drawIso(batch, shapeDrawer, delta)
            spriteNodeTree.rotateBy(rotation)
            spriteNodeTree.update(delta)
            for (node in spriteNodeTree.flatAndSorted()) {
                val pos2d = vec2()
                val pos = node.globalPosition3d
                if(node is AnimatedSpriteNode3d) {
                    batch.draw(node.sprite, pos2d.x + pos.x - node.sprite.offset.x, pos2d.y + pos.y - node.sprite.offset.y)
                    pos2d.x += 50f
                    batch.draw(node.sprite, pos2d.x + pos.y - node.sprite.offset.x, pos2d.y + pos.z - node.sprite.offset.y)
                    pos2d.x += 50f
                    batch.draw(node.sprite, pos2d.x + pos.z - node.sprite.offset.x, pos2d.y + pos.x - node.sprite.offset.y)
                    pos2d.x += 50f
                    batch.draw(node.sprite, pos2d.x + node.isoPosition.x - node.sprite.offset.x, pos2d.y + node.isoPosition.y - node.sprite.offset.y)
                } else {
                    shapeDrawer.filledCircle(pos2d.x + pos.x, pos2d.y + pos.y, 5f, node.color)
                    pos2d.x += 50f
                    shapeDrawer.filledCircle(pos2d.x + pos.y, pos2d.y + pos.z, 5f, node.color)
                    pos2d.x += 50f
                    shapeDrawer.filledCircle(pos2d.x + pos.z, pos2d.y + pos.x, 5f, node.color)
                    pos2d.x += 50f
                    shapeDrawer.filledCircle(pos2d.x + node.isoPosition.x, pos2d.y + node.isoPosition.y, 5f, node.color)
                }

//                node.drawIso(batch, shapeDrawer, delta, false)
//                node.draw2d(batch, shapeDrawer, delta, false, vec2(50f, 0f))
//                node.draw2d(batch, shapeDrawer, delta, false, vec2(-50f, 0f), true)
            }


            //shapeDrawer.line(pointsCloud.position, mousePosition, 1f)


            shapeDrawer.filledCircle(mousePosition, 1.5f, Color.RED)
//            shapeDrawer.setColor(Color.YELLOW)
//            for (arm in triangle.arms) {
//                shapeDrawer.line(arm.first.toIsometric(), arm.second.toIsometric())
//            }

//            shapeDrawer.filledPolygon(triangle.polygonB)


        }
    }
}

/**
 * Rething coordinates to be more standard, ie x horizontal, y vertical and then z as depth,
 * because it makes more sense conceptually with what you want to do. It doesn't matter, but still.
 *
 *
 */

fun Node3d.flatAndSorted(): List<Node3d> {
    return listOf(
        this,
        *children.asSequence().selectRecursive { children.asSequence() }.toList().toTypedArray()
    )//.sortedWith(compareBy<Node3d> { it.globalPosition3d.y }.thenBy { it.globalPosition3d.x }.thenBy { it.globalPosition3d.z })
//    )// .sortedBy { it.globalPosition3d.z }
}

fun <T> Sequence<T>.selectRecursive(recursiveSelector: T.() -> Sequence<T>): Sequence<T> = flatMap {
    sequence {
        yield(it)
        yieldAll(it.recursiveSelector().selectRecursive(recursiveSelector))
    }
}

