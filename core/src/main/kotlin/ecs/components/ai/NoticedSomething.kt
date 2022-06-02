package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2

/*
TODO: THis should not contain positional data, instead that should
be used in the KnownPosition Component, so that all positions are
always handled in that class.
 */

class NoticedSomething:Component, Pool.Poolable {
    val noticedWhere = vec2()
    override fun reset() {
        noticedWhere.set(Vector2.Zero)
    }

    override fun toString(): String {
        return "notice"
    }

}