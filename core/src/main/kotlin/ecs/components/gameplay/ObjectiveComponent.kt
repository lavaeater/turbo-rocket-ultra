package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ObjectiveComponent: Component, Pool.Poolable {
    var touched = false

    override fun reset() {
        touched = false
    }
}