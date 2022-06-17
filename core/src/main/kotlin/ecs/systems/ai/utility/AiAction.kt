package ecs.systems.ai.utility

import com.badlogic.ashley.core.Entity

abstract class AiAction {
    val considerations = mutableListOf<Consideration>()

    /***
     * This is open so we can simply implement a static score for something
     * like the Amble task - a task that will be performed unless some other task
     * gets a higher average score - very likely since most of my considerations would be
     * simply yes or no values
     */
    open fun score(entity: Entity): Double {
        return considerations.map { it.normalizedScore(entity) }.average()
    }

    abstract fun abort(entity: Entity)

    abstract fun act(entity: Entity, deltaTime: Float)
}