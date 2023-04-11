package screens.concept

import ktx.math.vec2
import tru.CardinalDirection

object CardinalToAngles {
    val cardinals = mapOf(
        221f..320f to CardinalDirection.South,
        321f..360f to CardinalDirection.East,
        0f..40f to CardinalDirection.East,
        41f..140f to CardinalDirection.North,
        141f..220f to CardinalDirection.West
    )

    val cardinalAbsoluteAngles = mapOf(
        CardinalDirection.East to 0f,
        CardinalDirection.South to 270f,
        CardinalDirection.West to 180f,
        CardinalDirection.North to 90f
    )

    val cardinalVectors = mapOf(
        CardinalDirection.East to vec2(1f, 0f),
        CardinalDirection.South to vec2(0f, -1f),
        CardinalDirection.West to vec2(-1f, 0f),
        CardinalDirection.North to vec2(0f, 1f)
    )

    fun angleToCardinal(angle: Float): CardinalDirection {
        val dirKey = cardinals.keys.firstOrNull { it.contains(angle) }
        return if (dirKey != null) cardinals[dirKey]!! else CardinalDirection.South
    }
}