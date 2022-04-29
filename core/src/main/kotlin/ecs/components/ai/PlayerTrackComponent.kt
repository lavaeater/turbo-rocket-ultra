package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import gamestate.Player

class PlayerTrackComponent : Component, Pool.Poolable {
    var player: Player? = null
    override fun reset() {
        player = null
    }
}