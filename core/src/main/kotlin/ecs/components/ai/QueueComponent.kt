package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue

open class QueueComponent<T>: Component, Pool.Poolable {
    val queue = Queue<T>()
    override fun reset() {
        queue.clear()
    }
}