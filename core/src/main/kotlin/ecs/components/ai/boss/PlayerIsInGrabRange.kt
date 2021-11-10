package ecs.components.ai.boss

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerIsInGrabRange : Component, Pool.Poolable {
    override fun reset() {

    }

    override fun toString(): String {
        return "Player Is In Grab Range"
    }

}