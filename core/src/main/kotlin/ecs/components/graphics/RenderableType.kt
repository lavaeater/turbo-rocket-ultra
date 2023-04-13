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
import eater.physics.addComponent
import ecs.components.gameplay.DestroyComponent
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
                val originX = textureRegion.regionWidth * textureRegionComponent.originX * textureRegionComponent.actualScale
                val originY = textureRegion.regionHeight * textureRegionComponent.originY * textureRegionComponent.actualScale
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

        private fun drawRifle(entity: Entity, character: CharacterComponent) {
            val start = character.worldAnchors["rightshoulder"]!!.cpy()
            rifle.rotation = character.aimVector.angleDeg()
            rifle.setPosition(start.x, start.y)
            val scaleX = MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue)
            rifle.setScale(scaleX, 1f)

            shapeDrawer.setColor(Color.GRAY)
            shapeDrawer.filledPolygon(rifle)
            shapeDrawer.setColor(Color.WHITE)
        }

        private val drawMethods: Map<CardinalDirection, List<(Entity, CharacterComponent)->Unit>> = mapOf(
            CardinalDirection.East to listOf(::drawLeftHand, ::drawRegion, ::drawRifle, ::drawRightHand),
            CardinalDirection.South to listOf(::drawRegion, ::drawLeftHand, ::drawRightHand, ::drawRifle),
            CardinalDirection.West to listOf(::drawRightHand, ::drawRifle, ::drawRegion, ::drawLeftHand),
            CardinalDirection.North to listOf(::drawLeftHand, ::drawRightHand, ::drawRifle, ::drawRegion)
        )

        private fun drawRegion(entity: Entity, character: CharacterComponent) {
            val textureRegionComponent = TextureRegionComponent.get(entity)
            val transform = TransformComponent.get(entity)

            if (textureRegionComponent.isVisible) {
                val textureRegion = textureRegionComponent.textureRegion
                val originX = textureRegion.regionWidth * textureRegionComponent.originX * textureRegionComponent.actualScale
                val originY = textureRegion.regionHeight * textureRegionComponent.originY * textureRegionComponent.actualScale
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

        private fun drawLeftHand(entity: Entity, character: CharacterComponent) {
            val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
            val leftShoulder = character.worldAnchors["leftshoulder"]!!.cpy()
            val lowerArmLength = 24f
            val upperArmLength = lowerArmLength * 5f / 8f
            val leftGripLength = 20f
            val leftHandDirection = character.aimVector.cpy().scl(leftGripLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
            val leftHandGripPoint = rightShoulder + leftHandDirection
            shapeDrawer.filledCircle(leftHandGripPoint, 4f, skinColor)
            leftHandDirection.set(leftHandGripPoint).sub(leftShoulder).nor()
            val leftDistance = leftHandGripPoint.dst(leftShoulder)

            val beta =
                MathUtils.acos((leftDistance.pow(2) + upperArmLength.pow(2) - lowerArmLength.pow(2)) / (2 * leftDistance * upperArmLength))
            val leftUpperArmVector = leftHandDirection.cpy().rotateRad(beta).scl(upperArmLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
            shapeDrawer.line(leftShoulder, leftShoulder + leftUpperArmVector, Color.BROWN, 6f)
            shapeDrawer.line(leftShoulder + leftUpperArmVector, leftHandGripPoint, Color.BROWN, 6f)
        }

        private fun drawRightHand(entity: Entity, character: CharacterComponent) {
            val rightShoulder = character.worldAnchors["rightshoulder"]!!.cpy()
            val rightGripLength = 8f
            val rightHandDirection = character.aimVector.cpy().scl(rightGripLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
            val rightHandGripPoint = rightShoulder + rightHandDirection
            shapeDrawer.filledCircle(rightHandGripPoint, 4f, skinColor)

            val lowerArmLength = 12f
            val upperArmLength = lowerArmLength * 5f / 8f
            rightHandDirection.set(rightHandGripPoint).sub(rightShoulder).nor()
            val rightDistance = rightHandGripPoint.dst(rightShoulder)

            val beta =
                MathUtils.acos((rightDistance.pow(2) + upperArmLength.pow(2) - lowerArmLength.pow(2)) / (2 * rightDistance * upperArmLength))

            val rightUpperArmVector = rightHandDirection.cpy().rotateRad(-beta).scl(upperArmLength).scl(MathUtils.lerp(0.5f, 1f, character.aimVector.x.absoluteValue))
            shapeDrawer.line(rightShoulder, rightShoulder + rightUpperArmVector, Color.BROWN, 6f)
            shapeDrawer.line(rightShoulder + rightUpperArmVector, rightHandGripPoint, Color.BROWN, 6f)
        }

        override fun render(entity: Entity, delta: Float) {
            val character = CharacterComponent.get(entity)
            for (drawMethod in drawMethods[character.cardinalDirection]!!)
                drawMethod(entity, character)
        }
    }

}