package extensions

import com.badlogic.gdx.math.Vector2
import tru.SpriteDirection


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