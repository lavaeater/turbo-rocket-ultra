package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2

class NoticedSomething:Component, Pool.Poolable {
    val noticedWhere = vec2()
    override fun reset() {
        noticedWhere.set(Vector2.Zero)
    }
}