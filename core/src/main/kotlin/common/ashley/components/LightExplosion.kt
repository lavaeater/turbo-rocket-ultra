package common.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class LightExplosion: Component, Pool.Poolable {
    var explosionTime = 0.1f
    var timeLeft = explosionTime
    override fun reset() {
        timeLeft = explosionTime
    }

    companion object {
        val mapper = mapperFor<LightExplosion>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): LightExplosion {
            return mapper.get(entity)
        }
    }
}