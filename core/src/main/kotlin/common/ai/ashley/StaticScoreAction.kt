package common.ai.ashley

import com.badlogic.ashley.core.Entity

abstract class StaticScoreAction(name: String, score: Float) : AiAction(name) {
    init {
        this.score = score
    }
    override fun updateScore(entity: Entity): Float {
        return score
    }
}