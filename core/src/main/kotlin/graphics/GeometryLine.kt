package graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import graphics.Geometry
import ktx.math.minus
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.properties.Delegates

class GeometryLine(c: Vector2, l: Float, val r: Float = 0f) : Geometry(c, r) {
    constructor(
        endPointOne: Vector2,
        endPointTwo: Vector2
    ) : this(
        vec2(
            endPointTwo.x - (endPointTwo.x - endPointOne.x) / 2f,
            endPointTwo.y - (endPointTwo.y - endPointOne.y) / 2f
        ), (endPointOne - endPointTwo).len(), (endPointTwo - endPointOne).angleDeg()
    )

    val actualRotation get() = worldRotation + r
    var length: Float by Delegates.observable(l, ::setDirty)
    var e1 = Vector2(0f, 0f)
    var e2 = Vector2(0f, 0f)

    override fun updateSelf() {
        val lv = vec2(length / 2f).rotateAroundDeg(vec2(0f, 0f), actualRotation)
        val ex = worldPosition.x + lv.x
        val ey = worldPosition.y + lv.y
        e1.set(ex, ey)
        e2.set(-ex, -ey)
    }

    override fun draw(shapeDrawer: ShapeDrawer) {
        super.draw(shapeDrawer)
        shapeDrawer.line(e1, e2, 1f)
        shapeDrawer.filledCircle(e1, 2.5f, Color.GREEN)
        shapeDrawer.filledCircle(e2, 2.5f, Color.BLUE)
        shapeDrawer.filledCircle(worldPosition, 1.5f, Color.RED)
    }
}