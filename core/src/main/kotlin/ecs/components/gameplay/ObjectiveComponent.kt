package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ObjectiveComponent: Component, Pool.Poolable {
    init {
        counter++
    }
    var id = "Constructed $counter"
    var touched = false

    override fun reset() {
        counter++
        id = "I have been reset $counter"
        touched = false
    }
    companion object {
        var counter = 0
    }
}