package ecs.components.enemy

import com.badlogic.ashley.core.Component
import ai.enemy.EnemyState
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import data.Player
import ecs.components.gameplay.TransformComponent
import ktx.math.random
import ktx.math.vec2

class BossComponent : Component, Pool.Poolable {
    override fun reset() {
    }
}

class EnemyComponent : Component, Pool.Poolable {
    var lastShotAngle = 0f
    var rushSpeed = 15f
    var fieldOfView = 180f
    var viewDistance = 90f
    var speed = 5f

    val directionVector = vec2()
    var health = 100f
    lateinit var lastHitBy: Player

    val isDead get() = health <= 0f

    var timeRemaining = 0f
    private set

    //PathFinding, useful everywhere
    var nextPosition = vec2()
    val path = Queue<Vector2>()
    var needsNewNextPosition = true

    fun takeDamage(damage: Float, player: Player) {
        health -= damage
        lastHitBy = player
    }

    fun takeDamage(range: ClosedFloatingPointRange<Float>, player: Player) {
        health -= range.random()
        lastHitBy = player
    }

    fun coolDown(deltaTime: Float) {
        timeRemaining-= deltaTime
        timeRemaining.coerceAtLeast(0f)
    }

    override fun reset() {
        nextPosition.setZero()
        path.clear()
        needsNewNextPosition = true
        fieldOfView = 90f
        speed = 2.5f
        viewDistance = 30f
        directionVector.set(Vector2.Zero)
        health = 100f
        timeRemaining = 0f
    }
}