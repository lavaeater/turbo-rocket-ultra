package ecs.components

import com.badlogic.ashley.core.Component

class EnemyComponent(health: Int = 25) : Component {
    var health = health
    private set
    fun takeDamage(range: IntRange) {
        health -= range.random()
    }
}