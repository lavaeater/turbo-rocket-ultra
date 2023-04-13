package ecs.components.fx

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2
import tru.Assets

class SplatterComponent : Component, Pool.Poolable {

    companion object {
        val mapper = mapperFor<SplatterComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): SplatterComponent {
            return mapper.get(entity)
        }
    }

    var splatterEffect: ParticleEffectPool.PooledEffect = Assets.splatterEffectPool.obtain()
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

