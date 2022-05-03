package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import data.Player
import ktx.math.vec2

class KnownPosition: Component, Pool.Poolable {
    val position = vec2()
    override fun reset() {
        position.setZero()
    }

}

class IsAwareOfPlayer : Component, Pool.Poolable {
    var player: Player? = null
    override fun reset() {
        player = null
    }
    override fun toString(): String {
        return "Tracking Player"
    }
}