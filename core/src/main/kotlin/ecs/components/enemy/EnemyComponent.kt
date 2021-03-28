package ecs.components.enemy

import com.badlogic.ashley.core.Component
import ai.enemy.EnemyState
import ecs.components.gameplay.TransformComponent
import ktx.math.vec2

class EnemyComponent(health: Int = 25) : Component {
    var speed = 2.5f
    var maxNumberOfScans = 0
    var scanCount = 0
    var keepScanning = true
    val scanVector = vec2()
    var needsScanVector = true
    var chaseTransform: TransformComponent? = null
    val directionVector = vec2()
    val scanVectorStart = vec2()
    val scanVectorEnd = vec2()

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