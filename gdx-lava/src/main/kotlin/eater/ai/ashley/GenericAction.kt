package eater.ai.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils

class GenericAction(
    name: String,
    private val scoreFunction: (entity: Entity) -> Float,
    private val abortFunction: (entity: Entity) -> Unit,
    private val actFunction: (entity: Entity, deltaTime:Float) -> Boolean,
    scoreRange: ClosedFloatingPointRange<Float> = 0f..1f): AiAction(name, scoreRange) {
    override fun abort(entity: Entity) {
        abortFunction(entity)
    }

    override fun act(entity: Entity, deltaTime: Float) : Boolean {
        return actFunction(entity, deltaTime)
    }

    override fun updateScore(entity: Entity): Float {
        score = MathUtils.map(0f,1f, scoreRange.start, scoreRange.endInclusive, scoreFunction(entity))
        return score
    }
}