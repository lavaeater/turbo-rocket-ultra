package eater.ai.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils

abstract class AiAction(val name: String, val scoreRange: ClosedFloatingPointRange<Float> = 0f..1f) {
    val considerations = mutableListOf<Consideration>()
    lateinit var interpolation: Interpolation
    var score: Float = 0f
    protected set

    /***
     * This is open so we can simply implement a static score for something
     * like the Amble task - a task that will be performed unless some other task
     * gets a higher average score - very likely since most of my considerations would be
     * simply yes or no values
     *
     * Can be overridden to affect the value of the score with some interpolations,
     * considerations to be taken if there are more than one factor in the scoring,
     * etc etc
     * To take time into consideration, use the timepiece func in gdx-ai
     */
    open fun updateScore(entity: Entity): Float {
        if(!::interpolation.isInitialized) {
            interpolation = Interpolation.PowOut(considerations.count())
        }
        score = MathUtils.map(0f, 1f, scoreRange.start, scoreRange.endInclusive, interpolation.apply(considerations.map { it.normalizedScore(entity) }.average().toFloat()))
        return score
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
     *
     * returning true will trigger a re-scoring of all the actions
     * which COULD be used to trigger actions that we want to happen fast.
     *
     * This could also be implemented by some actions only updating their scores on a timer,
     * instead. Who knows.
     */
    abstract fun act(entity: Entity, deltaTime: Float) : Boolean
}