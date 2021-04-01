package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue

class FiredShotsComponent : Component, Pool.Poolable {
    val queue = Queue<Vector2>()
    override fun reset() {
        queue.clear()
    }
}