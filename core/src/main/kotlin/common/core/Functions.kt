package common.core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import eater.injection.InjectionContext.Companion.inject
import ktx.math.vec2

fun<T> selectedItemListOf(selectedItemUpdated: (Int, T)-> Unit = {_,_ ->}, vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList(selectedItemUpdated, items.toList())
    return list
}

fun<T> selectedItemListOf(vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList({ _, _ ->  }, items.toList())
    return list
}

fun world(): World {
    return inject()
}

fun engine() : Engine {
    return inject()
}

fun String.toColor(): Color {
    val parts = this.chunked(2)
    val r = MathUtils.norm(0f, 255f, Integer.decode("0x${parts[0]}").toFloat())
    val g = MathUtils.norm(0f, 255f, Integer.decode("0x${parts[1]}").toFloat())
    val b = MathUtils.norm(0f, 255f, Integer.decode("0x${parts[2]}").toFloat())
    return Color(r, g, b, 1f)
}

fun Rectangle.wholePoints():MutableList<Vector2> {
    val points = mutableListOf<Vector2>()
    for(x in this.x.toInt()..(this.x + this.width).toInt())
        for(y in this.y.toInt()..(this.y + this.height).toInt())
            points.add(vec2(x.toFloat(), y.toFloat()))
    return points
}
