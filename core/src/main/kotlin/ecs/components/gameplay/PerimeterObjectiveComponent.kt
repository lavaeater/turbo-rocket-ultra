package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PerimeterObjectiveComponent: Component, Pool.Poolable {
    var distance = 100f
    override fun reset() {
        distance = 100f
    }
}