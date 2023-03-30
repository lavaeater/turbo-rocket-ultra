package ecs.components.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ecs.systems.graphics.GameConstants
import ktx.ashley.mapperFor
import ktx.math.random

class AttackableProperties: Component, Pool.Poolable {
    var stunned = false
    var health = GameConstants.ENEMY_BASE_HEALTH
    var maxHealth = health
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
        health = if (randomValue < 5) GameConstants.ENEMY_BASE_HEALTH * 10f else GameConstants.ENEMY_BASE_HEALTH
        maxHealth = health
        stunned = false
    }

    companion object {
        val mapper = mapperFor<AttackableProperties>()
        fun get(entity: Entity):AttackableProperties {
            return mapper.get(entity)
        }

        fun takeDamageSafe(target: Entity, attacker: Entity, damage: Float) : Boolean {
            return if(has(target)) {
                get(target).takeDamage(damage, attacker)
                true
            } else false
        }

        fun has(entity:Entity) : Boolean {
            return mapper.has(entity)
        }
    }

}