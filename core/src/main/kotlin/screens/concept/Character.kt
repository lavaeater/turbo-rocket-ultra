package screens.concept

import com.badlogic.gdx.math.Vector2
import ktx.math.plus
import ktx.math.times
import ktx.math.vec2
import tru.CardinalDirection

class Character {
    var worldPosition = vec2()
    val direction = Direction()
    val forward = direction.forward

    val angleDegrees get() = direction.angleDegrees
    val cardinalDirection get() = direction.cardinalDirection
    var width = 32f
    var height = 32f
    var scale = 1.0f
    val center = Vector2.Zero.cpy()

    private val anchors = mapOf(
        CardinalDirection.East to
                mapOf(
                    "rightshoulder" to vec2(-0.25f, 0.5f),
                    "leftshoulder" to vec2(0f, -0.25f)
                ),
        CardinalDirection.South to
                mapOf(
                    "rightshoulder" to vec2(0.25f, 0.5f),
                    "leftshoulder" to vec2(0.25f, -0.5f)
                ),
        CardinalDirection.West to
                mapOf(
                    "rightshoulder" to vec2(0f, 0.25f),
                    "leftshoulder" to vec2(-0.25f, -0.5f)
                ),
        CardinalDirection.North to
                mapOf(
                    "rightshoulder" to vec2(-0.25f, 0.5f),
                    "leftshoulder" to vec2(-0.25f, -0.5f)
                )
    )

    val aimVector = Vector2.X.cpy()

    //the angle should be the cardinal directions angle for the anchor points - they are static!
    val worldAnchors
        get() = anchors[cardinalDirection]!!.map {
            it.key to worldPosition + vec2().set(it.value.x, it.value.y * 0.5f).times(32f)
                .setAngleDeg(direction.cardinalAngle - it.value.angleDeg())
        }.toMap()

}