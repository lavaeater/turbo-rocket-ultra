package ecs.components.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class Fitness : Component, Pool.Poolable {
    var fitness: Int = 0
    override fun reset() {
        fitness = 0
    }
}