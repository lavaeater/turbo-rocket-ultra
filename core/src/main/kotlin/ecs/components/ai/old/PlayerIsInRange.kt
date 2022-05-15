package ecs.components.ai.old

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerIsInRange : Component, Pool.Poolable {
    override fun reset() {

    }

    override fun toString(): String {
        return "Player Is In Range"
    }

}