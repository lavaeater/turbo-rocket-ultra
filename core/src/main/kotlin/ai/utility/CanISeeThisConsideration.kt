package ai.utility

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import components.AgentProperties
import components.Memory
import components.TransformComponent
import core.engine
import physics.createComponent
import ktx.ashley.allOf
import ktx.log.debug
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType


class DoIRememberThisConsideration<ToRemember: Component>(
private val toRemember: KClass<ToRemember>
) : Consideration("Do you remember?") {
    override fun normalizedScore(entity: Entity): Float {
        val memory = ensureMemory(entity)
        return if(memory.seenEntities.containsKey(toRemember.starProjectedType) && memory.seenEntities[toRemember.starProjectedType]!!.isNotEmpty()) 1f else 0f
    }
}

class CanISeeThisConsideration<ToLookFor : Component>(
    private val lookFor: KClass<ToLookFor>
) : Consideration("Can I See ") {
    private val entitiesToLookForFamily = allOf(lookFor, TransformComponent::class).get()
    private val engine by lazy { engine() }
    override fun normalizedScore(entity: Entity): Float {
        val agentProps = AgentProperties.get(entity)
        val memory = ensureMemory(entity)
        if (!memory.seenEntities.containsKey(lookFor.starProjectedType)) {
            debug { "Create memory for ${lookFor.starProjectedType}" }
            memory.seenEntities[lookFor.starProjectedType] = mutableMapOf()
        }
        val seenEntities = memory.seenEntities[lookFor.starProjectedType]!!
        val agentPosition = TransformComponent.get(entity).position
        val inRange = engine.getEntitiesFor(entitiesToLookForFamily)
            .filter { TransformComponent.get(it).position.dst(agentPosition) < agentProps.viewDistance }
            .filter {
                canISeeYouFromHere(
                    agentPosition,
                    agentProps.directionVector,
                    TransformComponent.get(it).position,
                    agentProps.fieldOfView
                )
            }
        debug { "CanISeeThisConsideration found ${inRange.size} entities in range and in the field of view" }
        for (potential in inRange) {
            val entityPosition = TransformComponent.get(potential).position
            if (hasLineOfSight(agentPosition, entityPosition, potential)) {
                debug { "CanISeeThisConsideration - entity at $entityPosition can be seen" }
                seenEntities[potential] = memory.memoryLifeSpan
            }
        }
        return if (seenEntities.any() ) {
            1f
        }else 0f
    }
}

fun ensureMemory(
    entity: Entity
): Memory {
    val memory = if (!Memory.has(entity)) {
        val component = engine().createComponent<Memory>()
        entity.add(component)
        component
    } else {
        Memory.get(entity)
    }
    return memory
}