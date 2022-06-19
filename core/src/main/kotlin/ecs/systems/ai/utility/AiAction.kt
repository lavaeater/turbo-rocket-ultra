package ecs.systems.ai.utility

import com.badlogic.ashley.core.Entity

abstract class AiAction(name: String) {
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

    /**
     * Removes all components, settings etc related to this
     * action from the supplied entity - or puts them in a
     * paused state if the normal behavior is for the entity
     * to continue where it left off later
     */
    abstract fun abort(entity: Entity)

    /**
     * Does the thing it should do
     * Remember that actions should not hold their own state,
     * state should rather be kept on the entities themselves.
     */
    abstract fun act(entity: Entity, deltaTime: Float)
}