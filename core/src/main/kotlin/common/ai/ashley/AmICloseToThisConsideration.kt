package common.ai.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import eater.core.engine
import eater.ecs.ashley.components.TransformComponent
import ktx.ashley.allOf
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

class AmICloseToThisConsideration<ToLookFor : Component>(
    private val lookFor: KClass<ToLookFor>,
    private val distance: Float
) : Consideration("Am I Close to This?") {
    private val entitiesToLookForFamily = allOf(lookFor, TransformComponent::class).get()
    private val engine by lazy { engine() }
    override fun normalizedScore(entity: Entity): Float {
        val position = TransformComponent.get(entity).position
        val closeBums = engine.getEntitiesFor(entitiesToLookForFamily)
            .filter { TransformComponent.get(it).position.dst(position) < distance }
        if (closeBums.any()) {
            val memory = ensureMemory(entity)
            if (!memory.closeEntities.containsKey(lookFor.starProjectedType)) {
                memory.closeEntities[lookFor.starProjectedType] = mutableMapOf()
            }
            closeBums.map { memory.closeEntities[lookFor.starProjectedType]!![it] = memory.memoryLifeSpan }
        }
        return if (closeBums.any()) 1.0f else 0.0f
    }
}