package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import input.ControlMapper
import input.NoOpUserInterfaceControl
import ktx.math.vec2
import tru.Assets
import ui.new.BoundTextureElement
import ui.new.Carousel

class TowerBuildingUiComponent: Component, NoOpUserInterfaceControl(), Pool.Poolable {
    lateinit var controlMapper: ControlMapper
    val towers = listOf("machinegun", "flamethrower", "noise")
    val ui = Carousel(towers, listOf(BoundTextureElement({t -> Assets.towers[t]!!})), offset = vec2(5f, 5f), position = vec2(50f, 400f))
    var cancel = false
    var select = false

    override fun left() {
        ui.previousItem()
    }

    override fun right() {
        ui.nextItem()
    }

    override fun cancel() {
        cancel = true
    }

    override fun select() {
        select = true
    }

    override fun reset() {
        //no op for now
    }
}