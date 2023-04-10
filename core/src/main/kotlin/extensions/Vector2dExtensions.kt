package extensions

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import screens.ui.MousePosition
import tru.CardinalDirection


fun Vector2.worldToNorm(): Vector2 {
    return this.worldToNorm(MousePosition.minX, MousePosition.maxX, MousePosition.minY, MousePosition.maxY)
}

fun Vector2.worldToNorm(minX: Float, maxX: Float, minY: Float, maxY: Float): Vector2 {
    return vec2(MathUtils.norm(minX, maxX, x), MathUtils.norm(minY, maxY, y))
}

fun Vector2.normToWorld(): Vector2 {
    return normToWorld(MousePosition.minX, MousePosition.maxX, MousePosition.minY, MousePosition.maxY)
}

fun Vector2.normToWorld(minX: Float, maxX: Float, minY: Float, maxY: Float): Vector2 {
    return vec2(MathUtils.lerp(minX, maxX, x), MathUtils.lerp(minY, maxY, y))
}
fun Vector2.spriteDirection(): CardinalDirection {
    return when (this.angleDeg()) {
        in 150f..209f -> CardinalDirection.East
        in 210f..329f -> CardinalDirection.North
        in 330f..360f -> CardinalDirection.West
        in 0f..29f -> CardinalDirection.West
        in 30f..149f -> CardinalDirection.South
        else -> CardinalDirection.South
    }
}

/***
 * Returns angle in degrees to @param positionVector
 */
fun Vector2.angleTo(positionVector: Vector2): Float {
    return (MathUtils.acos(this.dot(this.cpy().sub(positionVector).nor()))) * MathUtils.radiansToDegrees
}