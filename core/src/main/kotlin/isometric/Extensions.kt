package isometric

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

fun Vector2.toIsometric() : Vector2 = vec2(this.x - this.y, (this.x + this.y) / 2)

fun Vector2.toCartesian() : Vector2 = vec2((2 * this.y + this.x) / 2, (2 * this.y - this.x) / 2)

