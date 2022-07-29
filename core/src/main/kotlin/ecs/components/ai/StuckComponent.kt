package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StuckComponent: Component, Pool.Poolable {
    override fun reset() {}
}