package screens.concepts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import ktx.math.*
import screens.concepts.CharacterSprites.body
import screens.concepts.CharacterSprites.eye
import screens.concepts.CharacterSprites.hair
import screens.concepts.CharacterSprites.hand
import screens.concepts.CharacterSprites.head
import screens.concepts.CharacterSprites.leg
import screens.concepts.CharacterSprites.mouth
import screens.stuff.AnimatedSpriteNode3d
import screens.stuff.Node
import screens.stuff.Node3d

object CharacterSprites {
    val head by lazy { Texture(Gdx.files.internal("sprites/layered/head.png")) }
    val hair by lazy { Texture(Gdx.files.internal("sprites/layered/hair.png")) }
    val hand by lazy { Texture(Gdx.files.internal("sprites/layered/hand.png")) }
    val leg by lazy { Texture(Gdx.files.internal("sprites/layered/leg.png")) }
    val body by lazy { Texture(Gdx.files.internal("sprites/layered/body2.png")) }
    val headTop by lazy { Texture(Gdx.files.internal("sprites/layered/head_top.png")) }
    val eye by lazy { Texture(Gdx.files.internal("sprites/layered/eye.png")) }
    val mouth by lazy { Texture(Gdx.files.internal("sprites/layered/mouth.png")) }
}

object Node3dCreator {

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

    private fun getRotationUpdateAction3d(
        forNode: Node3d,
        bounce: Boolean = true,
        time: Float = 1f,
        rotation: Vector3 = vec3()
    ): (Node3d, Float) -> Unit {
        val basePosition = forNode.localPosition3d
        var elapsedTime = 0f
        val modVector = vec3()
        /*
        This one is a bit interesting. Because what
        we WANT to do is use THIS node as a rotation center
        to rotate ALL immediate children (not recursively) around
        this particular point somehow. doable

        We create some kind of min-max-vectors, like before, but with
        angles. Then we rotate all children around

        Ah, but our rotation methods assume a rotation BY something,
        which is different than simply taking the node and adding some values
        to it. But it should.

        It shouldn't rotate the global position, the global position should always be calculated
        from the local position, which is what we change.

        To make this easier, we can just do what we do below, where we change the local position
        3d - which is w
         */
        val minVector = vec3(-(rotation.x / 2f), -(rotation.y / 2f), -(rotation.z / 2f))
        val maxVector = vec3(rotation.x / 2f, rotation.y / 2f, rotation.z / 2f)
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

            /*
            Ah, I know now.

            The min value is actuall starting rotation - modValue
            RotateBy is the value we should reach (new rotation) minus current
            rotation.

            There you goo.
             */

            //node.rotateBy(node.rotationAroundYAxis - modVector.y)
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
    fun getSpriteNodeTree(): Node3d {
        return Node3d("player").apply {
            addChild(AnimatedSpriteNode3d("body", body, vec3(0f, 0f, 0f)).apply {
                updateActions += getSmoothUpdateAction3d(this, true, 0.5f, vec3(0f, 5f, 0f))
                val armVector = vec2(15f, 0f).rotateAroundDeg(Vector2.Zero, 90f)
                addChild(Node3d("left-shoulder", vec3(armVector.x, 8f, armVector.y)).apply {
                    updateActions += getSmoothUpdateAction3d(this, true, 1f, vec3(5f, 10f, -5f))
                    addChild(AnimatedSpriteNode3d("left-hand", hand, vec3(0f, -4f, 0f)))
                })
                armVector.rotateAroundDeg(Vector2.Zero, -180f)
                addChild(Node3d("right-shoulder", vec3(armVector.x, 8f, armVector.y)).apply {
                    updateActions += getSmoothUpdateAction3d(this, true, 1f, vec3(5f, 10f, -5f))
                    addChild(AnimatedSpriteNode3d("right-hand", hand, vec3(0f, -4f, 0f)))
                })

                val legVector = vec2(7f).rotateAroundDeg(Vector2.Zero, 90f)
                addChild(AnimatedSpriteNode3d("leg", leg, vec3(legVector.x, -15f, legVector.y)).apply {
                    updateActions += getSmoothUpdateAction3d(this, true, .5f, vec3(0f, -15f, 0f))
                })
                legVector.rotateAroundDeg(Vector2.Zero, -180f)
                addChild(AnimatedSpriteNode3d("leg", leg, vec3(legVector.x, -15f, legVector.y)).apply {
                    updateActions += getSmoothUpdateAction3d(this, true, .5f, vec3(0f, 15f, 0f))
                })
                addChild(AnimatedSpriteNode3d("head", head, vec3(0f, 17f, 0f)).apply {
                    addChild(AnimatedSpriteNode3d("head", hair, vec3(1f, 5f, 1f)))
                    addChild(AnimatedSpriteNode3d("head", hair, vec3(-1f, 5f, -1f)))
                    addChild(AnimatedSpriteNode3d("head", hair, vec3(1f, 5f, -1f)))
                    addChild(AnimatedSpriteNode3d("head", hair, vec3(-1f, 5f, 1f)))
                })
                addChild(AnimatedSpriteNode3d("head", head, vec3(0f, 15f, 0f)))
                addChild(AnimatedSpriteNode3d("head", head, vec3(0f, 16f, -1f)))
                addChild(AnimatedSpriteNode3d("head", head, vec3(0f, 16f, 1f)).apply {

                    val eyeVector1 = vec2(9f).rotateAroundDeg(Vector2.Zero, 30f)
                    val eyeVector2 = vec2(9f).rotateAroundDeg(Vector2.Zero, -30f)

                    addChild(AnimatedSpriteNode3d("mouth", mouth, vec3(8f, 1f, 0f), color = Color.GREEN))
                    addChild(
                        AnimatedSpriteNode3d(
                            "eye",
                            eye,
                            vec3(eyeVector1.x, 4f, eyeVector1.y),
                            color = Color.GREEN
                        )
                    )
                    addChild(AnimatedSpriteNode3d("eye", eye, vec3(eyeVector2.x, 4f, eyeVector2.y), color = Color.BLUE))
                })
            })

        }
    }
}