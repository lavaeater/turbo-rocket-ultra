package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import data.Player

class PlayerComponent: Component, Pool.Poolable {
    lateinit var player: Player
    override fun reset() {
        //No-op
    }
}