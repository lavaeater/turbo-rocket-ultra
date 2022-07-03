package ai.utility

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Interpolation
import physics.attackables

sealed class Consideration(val name: String, val scoreFunction: (entity: Entity) -> Float = { 0f }, val interpolation: Interpolation = Interpolation.linear) {
    open fun normalizedScore(entity: Entity): Float {
        return interpolation.apply(scoreFunction(entity))
    }

    object MyHealthConsideration: Consideration("My Health", { entity ->
        val attackables = entity.attackables()
        attackables.health / attackables.maxHealth
    })
}