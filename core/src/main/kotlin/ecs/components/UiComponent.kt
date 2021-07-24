package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2
import ui.new.BoundTextElement
import ui.new.Carousel

class UiComponent: Component, Pool.Poolable {

    val towers = listOf("Machine Gun", "Grenade", "Flame")
    val ui = Carousel(towers, listOf(BoundTextElement({ p -> p })), position = vec2(50f, 400f))

    override fun reset() {
        //no op for now
    }
}