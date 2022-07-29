package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import data.Player

class BulletComponent: Component, Pool.Poolable {
    lateinit var player: Player
    var damage = 0f
    override fun reset() {
        damage = 0f
    }
}