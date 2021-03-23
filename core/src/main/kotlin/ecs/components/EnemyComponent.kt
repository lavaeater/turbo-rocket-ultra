package ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Queue
import ecs.systems.EnemyState
import ktx.math.vec2

class EnemyComponent(health: Int = 25) : Component {
    var maxNumberOfScans = 0
    var scanCount = 0
    var endAngle = 0f
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