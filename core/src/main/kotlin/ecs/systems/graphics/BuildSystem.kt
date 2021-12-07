package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerControlComponent
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import factories.blockade
import factories.tower
import factories.world
import injection.Context.inject
import ktx.ashley.allOf
import ktx.box2d.KtxQueryCallback
import ktx.box2d.query
import ktx.graphics.use
import map.grid.GridMapSection.Companion.scaledHeight
import map.grid.GridMapSection.Companion.scaledWidth
import map.grid.GridMapSection.Companion.tileHeight
import map.grid.GridMapSection.Companion.tileScale
import map.grid.GridMapSection.Companion.tileWidth
import physics.*
import tru.Assets
import tru.SpriteDirection



//Should render after map, before entities, that's the best...
class BuildSystem(private val debug: Boolean) : IteratingSystem(
    allOf(
        TransformComponent::class,
        PlayerControlComponent::class
    ).get()) {
    val batch by lazy { inject<PolygonSpriteBatch>() }
    val shapeDrawer by lazy { Assets.shapeDrawer }
    private val cursorColor = Color(0f, 1f, 0f, 0.3f)
    private val otherColor = Color(1f, 0f, 0f, 0.3f)

    private val buildables by lazy { Assets.buildables }


    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.playerControl()
        if (controlComponent.isInBuildMode) {
            val buildComponent = entity.build()
            val position = entity.transform().position
            val offset = CompassDirection.directionOffsets[controlComponent.compassDirection]!!

            val texture = buildables.first()
            val textureX = position.tileWorldX() + (offset.x * scaledWidth)
            val textureY = position.tileWorldY() + (offset.y * scaledHeight)

            val cursorX = position.tileWorldX() + (offset.x * scaledWidth)// + texture.offsetX * tileScale / 2
            val cursorY = position.tileWorldY() + (offset.y * scaledHeight)// + texture.offsetY * tileScale / 2

            val bodyX = position.tileWorldX() + (offset.x * scaledWidth)
            val bodyY = position.tileWorldY() + (offset.y * scaledHeight)

            val pWidth = tileWidth * tileScale
            val pHeight = tileHeight * tileScale

            val qbX = cursorX + pWidth / 4
            val qbY = cursorY + pHeight / 4
            val qtX = qbX + pWidth / 2
            val qtY = qbY + pHeight / 2

            batch.use {
                batch.drawScaled(
                    buildComponent.buildables.selectedItem.sprite,
                    cursorX + scaledWidth,
                    cursorY + scaledHeight,
//                    textureX,
//                    textureY,
                    tileScale
                )
            }
            shapeDrawer.batch.use {
                shapeDrawer.filledRectangle(
                    cursorX,
                    cursorY,
                    pWidth,
                    pHeight,
                    cursorColor
                )

            if (debug) {
               shapeDrawer.filledRectangle(
                        position.tileWorldX(),
                        position.tileWorldY(),
                        pWidth,
                        pHeight,
                        otherColor
                    )
                shapeDrawer.filledCircle(textureX,textureY,.5f, Color.RED)
                shapeDrawer.filledCircle(cursorX,cursorY,.5f, Color.GREEN)
                shapeDrawer.filledCircle(bodyX,bodyY,.5f, Color.BLUE)
                shapeDrawer.rectangle(qbX,qbY, pWidth /2, pHeight / 2, Color.BLUE)
                }
            }
            if(controlComponent.buildIfPossible) {
                var checker = true
                world().query(qbX,
                    qbY,qtX, qtY, object: KtxQueryCallback {
                        override fun invoke(fixture: Fixture): Boolean {
                            if(!fixture.isPlayer())
                                checker = false
                            return checker
                        }
                    })

                if(checker) {
                    /*
                    Build different things, but how? Well, by using a list of buildable things that are all of them
                    methods with the same signature

                    Also, show a label on the player that indicates what will be built!

                    Also, let HUD have a reference to the WORLD camera, for easier projections!
                     */

                    blockade(cursorX + 2f, cursorY + 2f)
                }
            }
        }
    }
}

fun Vector2.spriteDirection(): SpriteDirection {
    return when (this.angleDeg()) {
        in 150f..209f -> SpriteDirection.East
        in 210f..329f -> SpriteDirection.North
        in 330f..360f -> SpriteDirection.West
        in 0f..29f -> SpriteDirection.West
        in 30f..149f -> SpriteDirection.South
        else -> SpriteDirection.South
    }
}