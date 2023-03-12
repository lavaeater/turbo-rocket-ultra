package eater.ai.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils

abstract class AlsoGenericAction(name:String): AiAction(name) {
    abstract fun scoreFunction(entity: Entity):Float

    override fun updateScore(entity: Entity): Float {
        score = MathUtils.map(0f,1f, scoreRange.start, scoreRange.endInclusive, scoreFunction(entity))
        return score
    }
}