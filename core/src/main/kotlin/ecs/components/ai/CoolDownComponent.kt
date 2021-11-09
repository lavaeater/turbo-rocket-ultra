package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.math.random

class GibComponent: CoolDownComponent() {
    var hasStopped = false
    override fun reset() {
        super.reset()
        hasStopped = false
    }
}

abstract class CoolDownComponent: Component, Pool.Poolable {
    var coolDownRange = 30f..60f
    var coolDown = coolDownRange.random()
    override fun reset() {
        coolDown = coolDownRange.random()
    }

}