package map.snake

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.math.random
import ktx.math.vec2

fun Rectangle.randomPoint() : Vector2 {
    return vec2(this.horizontalRange().random(), this.verticalRange().random())
}

fun Rectangle.verticalRange() : ClosedRange<Float> {
    return this.bottom()..this.top()
}

fun Rectangle.horizontalRange(): ClosedRange<Float> {
    return this.left()..this.right()
}

fun Rectangle.left(): Float {
    return x
}

fun Rectangle.right(): Float {
    return x + width
}

fun Rectangle.bottom(): Float {
    return y
}

fun Rectangle.verticalCenter(): Float {
    return y + height / 2
}

fun Rectangle.horizontalCenter(): Float {
    return x + width / 2
}

fun Rectangle.top(): Float {
    return y + height
}
fun <T> List<T>.random(): T {
    return this[(0 until this.size).random()]
}
