package ecs.components.gameplay

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import data.Player

class MolotovComponent: Component, Pool.Poolable {
    lateinit var player: Player
    override fun reset() {
    }
}