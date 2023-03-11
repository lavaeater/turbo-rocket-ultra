package common.ai.ashley

import com.badlogic.ashley.core.Entity

open class Consideration(
    val name: String,
    val scoreFunction: (entity: Entity) -> Float = { 0f }
) {
    open fun normalizedScore(entity: Entity): Float {
        return scoreFunction(entity)
    }

}