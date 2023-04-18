package ecs.components.graphics

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import eater.ecs.ashley.components.TransformComponent
import eater.ecs.ashley.components.character.CharacterComponent
import eater.injection.InjectionContext.Companion.inject
import eater.input.CardinalDirection
import eater.input.mouse.MousePosition
import eater.physics.addComponent
import ecs.components.gameplay.DestroyComponent
import ecs.components.player.PlayerControlComponent
import ktx.math.plus
import physics.*
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.absoluteValue
import kotlin.math.pow

sealed class RenderableType {
    val engine by lazy { inject<Engine>() }
    val batch by lazy {
        inject<PolygonSpriteBatch>()
    }
    val shapeDrawer by lazy { inject<ShapeDrawer>() }

    open fun render(entity: Entity, delta: Float) {
        throw NotImplementedError()
    }


    object Effect : RenderableType() {
        override fun render(entity: Entity, delta: Float) {
            if (entity.hasSplatter()) {
                val component = entity.splatterEffect()
                val effect = component.splatterEffect
                if (effect.isComplete) {
                    engine.removeEntity(entity)
                }
                if (!component.started) {
                    component.started = true
                    val emitter = effect.emitters.first()
                    emitter.setPosition(component.at.x, component.at.y)
                    val amplitude: Float = (emitter.angle.highMax - emitter.angle.highMin) / 2f
                    emitter.angle.setHigh(component.rotation + amplitude, component.rotation - amplitude)
                    emitter.angle.setLow(component.rotation)
                    emitter.start()
                }
                effect.update(delta)
                effect.draw(batch)
            }

            if (entity.hasEffect()) {
                val effectComponent = entity.effect()
                if (effectComponent.ready) {
                    val transform = entity.transform()
                    val effect = effectComponent.effect
                    if (effect.isComplete) {
                        entity.addComponent<DestroyComponent>()
                    }
                    for (emitter in effect.emitters) {
                        emitter.setPosition(transform.position.x, transform.position.y)
                        if (!effectComponent.started) {
                            val amplitude: Float = (emitter.angle.highMax - emitter.angle.highMin) / 2f
                            emitter.angle.setHigh(
                                effectComponent.rotation + amplitude,
                                effectComponent.rotation - amplitude
                            )
                            emitter.angle.setLow(effectComponent.rotation)
                            emitter.start()
                        }
                    }
                    if (!effectComponent.started) {
                        effectComponent.started = true
                    }
                    effect.update(delta)
                    effect.draw(batch)
                }
            }
        }
    }

    object Sprite : RenderableType() {
        override fun render(entity: Entity, delta: Float) {
            val transform = TransformComponent.get(entity)
            val textureRegionComponent = TextureRegionComponent.get(entity)

            if (textureRegionComponent.isVisible) {
                val textureRegion = textureRegionComponent.textureRegion
                val originX =
                    textureRegion.regionWidth * textureRegionComponent.originX * textureRegionComponent.actualScale
                val originY =
                    textureRegion.regionHeight * textureRegionComponent.originY * textureRegionComponent.actualScale
                val x =
                    transform.position.x - originX
                val y =
                    transform.position.y - originY
                val rotation =
                    if (textureRegionComponent.rotateWithTransform) transform.angleDegrees else 0f

                batch.draw(
                    textureRegion,
                    x,
                    y,
                    0f,
                    0f,
                    textureRegion.regionWidth.toFloat(),
                    textureRegion.regionHeight.toFloat(),
                    textureRegionComponent.actualScale,
                    textureRegionComponent.actualScale,
                    rotation
                )
            }
        }
    }


    object CharacterWithArms : RenderableType() {
        private val skinColor = Color(0.8f, 0.6f, 0.5f, 1f)
        private val rifle = Polygon(floatArrayOf(0f, 2f, 50f, 2f, 50f, -2f, 0f, -2f))
        private var drawRifleAndArms = true
        private var drawDebug = true
        private var aimX = 0f
        private var aimY = 0f


        private fun drawDebug(character: CharacterComponent) {
            if (drawDebug) {
                drawAnchors(character)
                renderAimVector(character)
                renderLineToMouse(character)
            }
        }

        private fun drawAnchors(character: CharacterComponent) {
            for ((key, point) in character.worldAnchors) {
                shapeDrawer.filledCircle(
                    point,
                    5f * character.scale,
                    if (key.contains("left")) Color.RED else Color.GREEN
                )
            }
        }

        private fun renderAimVector(character: CharacterComponent) {
            shapeDrawer.filledCircle(
                character
                    .worldPosition + (
                        character
                            .aimVector
                            .cpy()
                            .scl(
                                25f * character.scale
                            )
                        ),
                2.5f * character.scale,
                Color.YELLOW
            )
        }

        private fun renderLineToMouse(character: CharacterComponent) {
            val start = character.worldPosition + (character.aimVector.cpy().scl(25f * character.scale))
            val stop = MousePosition.worldPosition2D.cpy()
            shapeDrawer.line(start, stop, Color.YELLOW, character.scale)
            shapeDrawer.filledCircle(start, 2f * character.scale, Color.GREEN)
            shapeDrawer.filledCircle(stop, 2f * character.scale, Color.RED)
            shapeDrawer.filledCircle(MousePosition.worldPosition2D, 5f * character.scale, Color.WHITE)

            start.set(character.worldAnchors["rightshoulder"]!!)

            shapeDrawer.line(start, stop, Color.RED, character.scale)
        }

        private fun drawRifle(entity: Entity, character: CharacterComponent) {
            if (drawRifleAndArms) {
                val start = character.worldAnchors["rightshoulder"]!!.cpy()
                rifle.rotation = character.aimVector.angleDeg()
                rifle.setPosition(start.x, start.y)
                val scaleX = MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue)
                rifle.setScale(scaleX * character.scale, 1f * character.scale)

                shapeDrawer.setColor(Color.GRAY)
                shapeDrawer.filledPolygon(rifle)
                shapeDrawer.setColor(Color.WHITE)
            }
        }

        private val drawMethods: Map<CardinalDirection, List<(Entity, CharacterComponent) -> Unit>> = mapOf(
            CardinalDirection.East to listOf(::drawLeftArm, ::drawTorso, ::drawRifle, ::drawRightHand),
            CardinalDirection.South to listOf(::drawTorso, ::drawLeftArm, ::drawRightHand, ::drawRifle),
            CardinalDirection.West to listOf(::drawRightHand, ::drawRifle, ::drawTorso, ::drawLeftArm),
            CardinalDirection.North to listOf(::drawLeftArm, ::drawRightHand, ::drawRifle, ::drawTorso)
        )

        private fun drawRegion(entity: Entity, character: CharacterComponent) {
            val textureRegionComponent = TextureRegionComponent.get(entity)
            val transform = TransformComponent.get(entity)

            if (textureRegionComponent.isVisible) {
                val textureRegion = textureRegionComponent.textureRegion
                val originX =
                    textureRegion.regionWidth * textureRegionComponent.originX * textureRegionComponent.actualScale
                val originY =
                    textureRegion.regionHeight * textureRegionComponent.originY * textureRegionComponent.actualScale
                val x =
                    transform.position.x - originX
                val y =
                    transform.position.y - originY
                val rotation =
                    if (textureRegionComponent.rotateWithTransform) transform.angleDegrees else 0f

                batch.draw(
                    textureRegion,
                    x,
                    y,
                    0f,
                    0f,
                    textureRegion.regionWidth.toFloat(),
                    textureRegion.regionHeight.toFloat(),
                    textureRegionComponent.actualScale,
                    textureRegionComponent.actualScale,
                    rotation
                )
            }
        }

        /**
         * The torso could very well consist of drawing FOUR polygons
         */
        private fun drawTorso(entity: Entity, character: CharacterComponent) {
            /**
             * The torso is drawn as a polygon that changes as we go about our day.
             * The points defining the polygon are
             *
             * The right shoulder
             * The left shoulder
             * The right hip
             * The left hip
             */
            val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
            val leftShoulder = character.worldAnchors["leftshoulder"]!!.cpy()
            val rightHip = rightShoulder.cpy().sub(0f, 5f)
            val leftHip = leftShoulder.cpy().sub(0f, 5f)

            val rsf = character.worldAnchors["rightsf"]!!.cpy()
            val lsf = character.worldAnchors["leftsf"]!!.cpy()

            shapeDrawer.setColor(Color.BLUE)
            shapeDrawer.filledPolygon(
                floatArrayOf(
                    rightShoulder.x,
                    rightShoulder.y,
                    leftShoulder.x,
                    leftShoulder.y,
                    leftHip.x,
                    leftHip.y,
                    rightHip.x,
                    rightHip.y
                )
            )
            shapeDrawer.setColor(Color.CYAN)
            shapeDrawer.filledPolygon(
                floatArrayOf(
                    rightShoulder.x,
                    rightShoulder.y,
                    leftShoulder.x,
                    leftShoulder.y,
                    lsf.x,
                    lsf.y,
                    rsf.x,
                    rsf.y
                )
            )
//            rightShoulder
//            leftShoulder
//            rightHip
//            leftHip

//            shapeDrawer.setColor(Color.WHITE)
        }

        private fun drawLeftArm(entity: Entity, character: CharacterComponent) {
            /**
             * What is a leftarm?
             *
             * It is a bunch of points that are defined somehow
             * that we then want to draw lines and circles in relation to
             *
             * So, the left arm consists of:
             *
             * The Right Shoulder: a vector
             * The Left Shoulder: a vector
             * An Aim Vector
             * A Grip Point - defined as a distance N from the right shoulder in the direction of the aim vector
             * An upper arm - defined as a unit vector pointing from shoulder to grip point, rotated by angle Beta and scaled by the length of the upper arm
             *
             */
            if (drawRifleAndArms) {
                val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
                val leftShoulder = character.worldAnchors["leftshoulder"]!!.cpy()
                val lowerArmLength = 24f * character.scale
                val upperArmLength = lowerArmLength * 5f / 8f
                val leftGripLength = 20f * character.scale
                val leftHandDirection =
                    character
                        .aimVector
                        .cpy()
                        .scl(leftGripLength)
                        .scl(
                            MathUtils
                                .lerp(
                                    0.5f,
                                    1f,
                                    character
                                        .aimVector
                                        .x
                                        .absoluteValue
                                )
                        )
                val leftHandGripPoint = (rightShoulder + leftHandDirection)
                shapeDrawer.filledCircle(leftHandGripPoint, 4f * character.scale, skinColor)
                // set the direction to a unit vector pointing FROM the shoulder TO the hand
                leftHandDirection
                    .set(leftHandGripPoint)
                    .sub(leftShoulder)
                    .nor()

                // calculate distance from shoulder to hand
                val leftDistance = leftHandGripPoint.dst(leftShoulder)

                // calculate angle between shoulder and upper arm
                val beta =
                    MathUtils.acos(
                        (
                                leftDistance.pow(2) +
                                        upperArmLength.pow(2) -
                                        lowerArmLength.pow(2)) /
                                (2 * leftDistance * upperArmLength)
                    )

                val leftUpperArmVector =
                    leftHandDirection
                        .cpy()
                        .rotateRad(beta)
                        .scl(upperArmLength)
                        .scl(
                            MathUtils.lerp(
                                0.5f,
                                1f,
                                character
                                    .aimVector
                                    .y
                                    .absoluteValue
                            )
                        )
                shapeDrawer.line(
                    leftShoulder,
                    leftShoulder + leftUpperArmVector,
                    Color.BROWN,
                    6f * character.scale
                )
                shapeDrawer.line(
                    leftShoulder + leftUpperArmVector,
                    leftHandGripPoint,
                    Color.BROWN,
                    6f * character.scale
                )
            }
        }

        private fun drawRightHand(entity: Entity, character: CharacterComponent) {
            if (drawRifleAndArms) {
                val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
                val rightGripLength = 8f * character.scale
                val rightHandDirection = character.aimVector.cpy().scl(rightGripLength)
                    .scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
                val rightHandGripPoint = rightShoulder + rightHandDirection
                shapeDrawer.filledCircle(rightHandGripPoint, 4f * character.scale, skinColor)

                val lowerArmLength = 12f * character.scale
                val upperArmLength = lowerArmLength * 5f / 8f * character.scale
                rightHandDirection.set(rightHandGripPoint).sub(rightShoulder).nor()
                val rightDistance = rightHandGripPoint.dst(rightShoulder)

                val beta =
                    MathUtils.acos((rightDistance.pow(2) + upperArmLength.pow(2) - lowerArmLength.pow(2)) / (2 * rightDistance * upperArmLength))

                val rightUpperArmVector = rightHandDirection.cpy().rotateRad(-beta).scl(upperArmLength)
                    .scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
                shapeDrawer.line(rightShoulder, rightShoulder + rightUpperArmVector, Color.BROWN, 6f * character.scale)
                shapeDrawer.line(
                    rightShoulder + rightUpperArmVector,
                    rightHandGripPoint,
                    Color.BROWN,
                    6f * character.scale
                )
            }
        }

        override fun render(entity: Entity, delta: Float) {
            val character = CharacterComponent.get(entity)
            aimX = character.aimVector.x
            aimY = character.aimVector.y
            drawRifleAndArms = true
            if (PlayerControlComponent.has(entity) && !PlayerControlComponent.get(entity).aiming) {
                drawRifleAndArms = false
            }
            for (drawMethod in drawMethods[character.cardinalDirection]!!)
                drawMethod(entity, character)

            drawDebug(character)
        }
    }

}

/**
 * What is a leftarm?
 *
 * It is a bunch of points that are defined somehow
 * that we then want to draw lines and circles in relation to
 *
 * So, the left arm consists of:
 *
 * The Right Shoulder: a vector
 * The Left Shoulder: a vector
 * An Aim Vector
 * A Grip Point - defined as a distance N from the right shoulder in the direction of the aim vector
 * An upper arm - defined as a unit vector pointing from shoulder to grip point, rotated by angle Beta and scaled by the length of the upper arm
 *
 * OK; this actually seems quite hard to generalize, because it isn't general
 *
 * This is the definition of the LEFT arm, but the RIGHT arm doesn't need the left shoulder
 * and so on. Having them in their own functions is probably fine for now, but there is probably
 * some sweet generalizations to be made here.
 *
 * Lets do a torso!
 */

class ParameterizedVectorThingie {

}

