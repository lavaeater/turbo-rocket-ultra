package ecs.systems.graphics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerControlComponent
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import factories.blockade
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


//Should render after map, before entities, that's the best...
class BuildSystem(private val debug: Boolean) : IteratingSystem(
    allOf(
        TransformComponent::class,
        PlayerControlComponent::class
    ).get()) {
    val batch by lazy { inject<PolygonSpriteBatch>() }
    val shapeDrawer by lazy { Assets.shapeDrawer }
    private val cursorColor = Color(0f, 1f, 0f, 1f)
    private val otherColor = Color(1f, 0f, 0f, 1f)

    private val buildables by lazy { Assets.buildables }


    /*
    This doesn't work because we are rendering this after everything else is rendered and we have
    like the fx system that needs to capture this stuff to be able to render it.

    For the time being we could remove fx, but we want to be able to use frame buffer stuff for FX, so
    ALL rendering should probably be baked into the rendering pipeline. But what would THIS then do?

    Well, this could in fact just control the position of an entity with a sprite that is set by this code,
    making the rendering pipeline render that particular entity and having the position simply set by this code.

    That would take the "specialness" out of that rendering pipeline, which would help in many cases.

     */

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val controlComponent = entity.playerControl()
        val buildComponent = entity.build()

        if (controlComponent.isInBuildMode) {
            val cursorEntity = buildComponent.buildCursorEntity!!
            cursorEntity.sprite().sprite = buildComponent.buildables.selectedItem.sprite

            /*
            Now all we have to fix is simply... setting the position of the sprite
            AND removing the entity if build mode is not relevant any more.
             */

            val builderPosition = entity.transform().position
            val cursorOffset = CompassDirection.directionOffsets[controlComponent.compassDirection]!!

            val texture = buildables.first()
            val textureX = builderPosition.tileWorldX() + (cursorOffset.x * scaledWidth)
            val textureY = builderPosition.tileWorldY() + (cursorOffset.y * scaledHeight)

            val cursorX = builderPosition.tileWorldX() + (cursorOffset.x * scaledWidth)// + texture.offsetX * tileScale / 2
            val cursorY = builderPosition.tileWorldY() + (cursorOffset.y * scaledHeight)// + texture.offsetY * tileScale / 2

            val bodyX = builderPosition.tileWorldX() + (cursorOffset.x * scaledWidth)
            val bodyY = builderPosition.tileWorldY() + (cursorOffset.y * scaledHeight)

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
                        builderPosition.tileWorldX(),
                        builderPosition.tileWorldY(),
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
