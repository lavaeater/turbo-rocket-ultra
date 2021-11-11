package ecs.components.fx

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2
import tru.Assets

class SplatterComponent : Component, Pool.Poolable {
    var splatterEffect = Assets.splatterEffectPool.obtain()
    var started = false
    var at: Vector2 = vec2()
    var rotation: Float = 0f
    override fun reset() {
        at.setZero()
        rotation = 0f
        started = false
        splatterEffect.free()
        splatterEffect = Assets.splatterEffectPool.obtain()
    }
}

