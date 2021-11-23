package ecs.components.fx

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class CreateEntityComponent: Component, Pool.Poolable {
    var creator: () -> Unit = {}
    override fun reset() {
        creator = {}
    }
}