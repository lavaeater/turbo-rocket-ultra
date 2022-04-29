package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DestroyComponent: Component, Pool.Poolable {
    override fun reset() {
    }
}