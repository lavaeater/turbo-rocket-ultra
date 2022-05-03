package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import data.Player

class IsAwareOfPlayer : Component, Pool.Poolable {
    var player: Player? = null
    override fun reset() {
        player = null
    }
    override fun toString(): String {
        return "Tracking Player"
    }
}