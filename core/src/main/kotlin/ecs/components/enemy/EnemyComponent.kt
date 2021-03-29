package ecs.components.enemy

import com.badlogic.ashley.core.Component
import ai.enemy.EnemyState
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.gameplay.TransformComponent
import ktx.math.vec2

class EnemyComponent : Component, Pool.Poolable {
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
    var health = 25
    private set

    var timeRemaining = 0f
    private set

    fun takeDamage(range: IntRange) {
        health -= range.random()
    }

    fun coolDown(deltaTime: Float) {
        timeRemaining-= deltaTime
        timeRemaining.coerceAtLeast(0f)
    }

    override fun reset() {
        speed = 2.5f
        keepScanning = true
        scanVector.set(Vector2.Zero)
        needsScanVector = true
        chaseTransform = null
        directionVector.set(Vector2.Zero)
        scanVectorStart.set(Vector2.Zero)
        scanVectorEnd.set(Vector2.Zero)
        health = 25
        timeRemaining = 0f
    }
}