package ai.utility

import com.badlogic.ashley.core.Entity

abstract class StaticScoreAction(name: String, private val score: Float) : AiAction(name) {
    override fun score(entity: Entity): Double {
        return score.toDouble()
    }
}