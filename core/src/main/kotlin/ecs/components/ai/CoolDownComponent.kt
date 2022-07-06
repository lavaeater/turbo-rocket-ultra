package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.math.random

abstract class CoolDownComponent: Component, Pool.Poolable {
    var coolDownRange = 30f..60f
    var coolDown = coolDownRange.random()
    fun ready(deltaTime:Float): Boolean  {
        coolDown -= deltaTime
        if(coolDown < 0f) {
            coolDown = coolDownRange.random()
            return true
        }
        return false
    }

    fun restartCooldown() {
        coolDown = coolDownRange.random()
    }
    override fun reset() {
        coolDown = coolDownRange.random()
    }

}