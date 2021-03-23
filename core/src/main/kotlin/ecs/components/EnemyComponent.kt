package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Queue
import ecs.systems.EnemyState
import ktx.math.vec2

class EnemyComponent(health: Int = 25) : Component {
    val directionVector = vec2()

    var state : EnemyState = EnemyState.Ambling
    private set
    var health = health
    private set

    var timeRemaining = 0f
    private set

    fun newState(newState: EnemyState, time: Float = 0f) {
        state = newState
        timeRemaining = time
    }

    fun takeDamage(range: IntRange) {
        health -= range.random()
    }

    fun coolDown(deltaTime: Float) {
        timeRemaining-= deltaTime
        timeRemaining.coerceAtLeast(0f)
    }
}