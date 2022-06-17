package ecs.systems.ai.utility

import com.badlogic.ashley.core.Entity

abstract class StaticScoreAction(private val score: Float) : AiAction() {
    override fun score(entity: Entity): Double {
        return score.toDouble()
    }
}