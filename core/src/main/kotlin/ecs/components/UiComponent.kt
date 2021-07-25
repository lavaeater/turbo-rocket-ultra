package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2
import tru.Assets
import ui.new.BoundTextureElement
import ui.new.Carousel

class UiComponent: Component, Pool.Poolable {

    val towers = listOf("machinegun", "flamethrower", "noise")
    val ui = Carousel(towers, listOf(BoundTextureElement({t -> Assets.towers[t]!!})), offset = vec2(5f, 5f), position = vec2(50f, 400f))

    override fun reset() {
        //no op for now
    }
}