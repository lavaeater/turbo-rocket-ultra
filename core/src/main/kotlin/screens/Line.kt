package screens

import com.badlogic.gdx.math.Vector2
import ktx.math.ImmutableVector2
import ktx.math.minus
import ktx.math.vec2
import kotlin.properties.Delegates

class Line(c: Vector2, l: Float, r: Float = 0f) {
    constructor(
        endPointOne: Vector2,
        endPointTwo: Vector2
    ) : this(
        vec2(
            endPointTwo.x - (endPointTwo.x - endPointOne.x) / 2f,
            endPointTwo.y - (endPointTwo.y - endPointOne.y) / 2f
        ), (endPointOne - endPointTwo).len(), (endPointTwo - endPointOne).angleDeg()
    )

    var x: Float by Delegates.observable(c.x) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var y: Float by Delegates.observable(c.y) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var length: Float by Delegates.observable(l) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var rotation: Float by Delegates.observable(r) { _, oldValue, newValue -> dirty = newValue != oldValue }
    var dirty = true

    fun update() {
        if (dirty) {
            center = ImmutableVector2(x, y)
            val lv = vec2(length / 2f).rotateAroundDeg(vec2(0f, 0f), rotation)
            val ex = x + lv.x
            val ey = y + lv.y
            e1 = ImmutableVector2(ex, ey)
            e2 = ImmutableVector2(-ex, -ey)
            dirty = false
        }
    }

    var center = ImmutableVector2(x, y)
        private set
        get() {
            update()
            return field
        }
    var e1 = ImmutableVector2(0f, 0f)
        private set
        get() {
            update()
            return field
        }
    var e2 = ImmutableVector2(0f, 0f)
        private set
        get() {
            update()
            return field
        }
}