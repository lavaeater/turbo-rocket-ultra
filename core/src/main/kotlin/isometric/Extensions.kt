package isometric

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

fun Vector2.toIso() : Vector2 = vec2(this.x - this.y, )