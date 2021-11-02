package map.snake

import com.badlogic.gdx.math.Rectangle

fun Rectangle.left(): Float {
    return x
}

fun Rectangle.right(): Float {
    return x + width
}

fun Rectangle.top(): Float {
    return y
}

fun Rectangle.verticalCenter(): Float {
    return y + height / 2
}

fun Rectangle.horizontalCenter(): Float {
    return x + width / 2
}

fun Rectangle.bottom(): Float {
    return y + height
}
fun <T> List<T>.random(): T {
    return this[(0 until this.size).random()]
}
