package ecs.components.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ecs.systems.graphics.GameConstants
import ktx.math.random

class AttackableProperties: Component, Pool.Poolable {
    var stunned = false
    var health = GameConstants.BASE_HEALTH
    lateinit var lastHitBy: Entity

    val isDead get() = health <= 0f
    fun takeDamage(damage: Float, entity: Entity) {
        health -= damage
        lastHitBy = entity
    }

    fun takeDamage(range: ClosedFloatingPointRange<Float>, entity: Entity) {
        health -= range.random()
        lastHitBy = entity
    }
    override fun reset() {
        val randomValue = (1..100).random()
        health = if (randomValue < 5) 1000f else 100f
        stunned = false
    }

}