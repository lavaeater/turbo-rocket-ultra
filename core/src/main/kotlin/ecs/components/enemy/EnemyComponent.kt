package ecs.components.enemy

import com.badlogic.ashley.core.Component
import ai.enemy.EnemyState
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.gameplay.TransformComponent
import ktx.math.vec2

class EnemyComponent : Component, Pool.Poolable {
    var speed = 1f

    val directionVector = vec2()
    var health = 25
    private set

    val isDead get() = health <= 0

    var timeRemaining = 0f
    private set

    fun takeDamage(damage: Int) {
        health -= damage
    }
    fun takeDamage(range: IntRange) {
        health -= range.random()
    }

    fun coolDown(deltaTime: Float) {
        timeRemaining-= deltaTime
        timeRemaining.coerceAtLeast(0f)
    }

    override fun reset() {
        speed = 2.5f
        directionVector.set(Vector2.Zero)
        health = 25
        timeRemaining = 0f
    }
}