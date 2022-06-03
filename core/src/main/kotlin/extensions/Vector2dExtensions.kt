package extensions

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2
import screens.MousePosition
import tru.SpriteDirection


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

/***
 * Returns angle in degrees to @param positionVector
 */
fun Vector2.angleTo(positionVector: Vector2): Float {
    return (MathUtils.acos(this.dot(this.cpy().sub(positionVector).nor()))) * MathUtils.radiansToDegrees
}