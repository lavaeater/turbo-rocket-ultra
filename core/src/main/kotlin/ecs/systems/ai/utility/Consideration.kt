package ecs.systems.ai.utility

import com.badlogic.ashley.core.Entity

abstract class Consideration {
    abstract fun normalizedScore(entity: Entity): Float
}