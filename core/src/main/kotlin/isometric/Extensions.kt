package isometric

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2


fun Vector2.toIsometric(vector: Vector2) {
    vector.set(this.x - this.y, (this.x + this.y) / 2)
}

fun Vector2.toIsometric() : Vector2 = vec2(this.x - this.y, (this.x + this.y) / 2)

fun Vector2.toCartesian() : Vector2 = vec2((2 * this.y + this.x) / 2, (2 * this.y - this.x) / 2)

fun Vector2.polygonFromPos(width: Float, height:Float): Polygon {
    //create 8 coordinates. transform these to isoMetric coords.
    val bottomLeft = vec2(this.x - width / 2, this.y  - height / 2).toIsometric()
    val topLeft = vec2(this.x - width / 2, this.y + height / 2).toIsometric()
    val topRight = vec2(this.x + width / 2, this.y + height / 2).toIsometric()
    val bottomRight = vec2(this.x + width / 2, this.y - height / 2).toIsometric()

    return Polygon(floatArrayOf(bottomLeft.x, bottomLeft.y, topLeft.x, topLeft.y, topRight.x, topRight.y, bottomRight.x, bottomRight.y))
}