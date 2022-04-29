package ecs.components.enemy

import com.badlogic.ashley.core.Component
import ai.enemy.EnemyState
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.gameplay.TransformComponent
import ktx.math.random
import ktx.math.vec2

class BossComponent : Component, Pool.Poolable {
    override fun reset() {
    }
}

class EnemyComponent : Component, Pool.Poolable {
    var rushSpeed = 15f
    var fieldOfView = 180f
    var viewDistance = 30f
    var speed = 1f

    val directionVector = vec2()
    var health = 100f

    val isDead get() = health <= 0f

    var timeRemaining = 0f
    private set

    fun takeDamage(damage: Float) {
        health -= damage
    }

    fun takeDamage(range: ClosedFloatingPointRange<Float>) {
        health -= range.random()
    }

    fun coolDown(deltaTime: Float) {
        timeRemaining-= deltaTime
        timeRemaining.coerceAtLeast(0f)
    }

    override fun reset() {
        fieldOfView = 90f
        speed = 2.5f
        viewDistance = 30f
        directionVector.set(Vector2.Zero)
        health = 100f
        timeRemaining = 0f
    }
}