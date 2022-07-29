package ecs.components.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class Enemy : Component, Pool.Poolable {
    override fun reset() {
    }
}