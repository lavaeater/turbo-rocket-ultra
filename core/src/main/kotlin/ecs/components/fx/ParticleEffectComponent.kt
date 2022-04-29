package ecs.components.fx

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.utils.Pool
import ktx.math.vec2

/**
 * A generic particle effect handler?
 */
open class ParticleEffectComponent: Component, Pool.Poolable {
    lateinit var _effect: ParticleEffect
    var effect: ParticleEffect
        get() = _effect
        set(value) {
            _effect = value
            ready = true
        }
    var ready = false
    var started = false
    var at = vec2()
    var rotation = 0f
    override fun reset() {
        if(ready) {
            effect.reset()
            ready = false
        }
        started = false
        at = vec2()
        rotation = 0f
    }
}

class FireEffectComponent : ParticleEffectComponent() {

}