package ecs.systems.ai.utility

import com.badlogic.ashley.core.Entity
import physics.attackables

class MyHealthConsideration : Consideration() {
    override fun normalizedScore(entity: Entity): Float {
        val attackables = entity.attackables()
        return attackables.health / attackables.maxHealth
    }
}