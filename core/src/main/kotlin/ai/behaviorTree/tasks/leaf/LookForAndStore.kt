package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.utility.canISeeYouFromHere
import ai.utility.hasLineOfSight
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import components.TransformComponent
import components.ai.PositionStorageComponent
import ktx.ashley.allOf
import ktx.log.debug
import physics.agentProps
import physics.transform
import kotlin.reflect.KClass

/**
 * Evolve this idea later, of being able to define some kind of storage
 * Component Dynamically
 *
 * Perhaps we have subtypes of
 *
 * Actually, if we just set a list of vectors, that list updates automagically
 * since the vectors are by ref, not val, which is very cool.
 *
 * Let's try that.
 */
class LookForAndStore<ToLookFor : Component, ToStoreIn : PositionStorageComponent>(
    val componentClass: KClass<ToLookFor>,
    val storageComponentClass: KClass<ToStoreIn>,
    val stop: Boolean
) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return LookForAndStore(componentClass, storageComponentClass, stop)
    }
    override fun cloneTask(): Task<Entity> {
        val clone = LookForAndStore(componentClass, storageComponentClass, stop)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }


    private val entitiesToLookForFamily = allOf(componentClass, TransformComponent::class).get()
    private val mapper by lazy { ComponentMapper.getFor(storageComponentClass.java) }
    override fun execute(): Status {
        /* We only, note, ONLY check if we can see entities
        of a certain type.
        We can see them if they are:
        1. Within range
        2. Within sight sector
        3. Not behind an obstacle

        This should add entities to some kind of collection of entities we see, in some kind of component.
        This component can then be used for other behaviors.


        Entities to find MUST have transformComponent!
        */
        val agentProps = entity.agentProps()
        if(stop)
            agentProps.speed = 0f
        val agentPosition = entity.transform().position
        /*
        Choose a random viewDirection within fov of current viewingdirection! Or some other technique
         */


        val inrangeEntities = engine.getEntitiesFor(entitiesToLookForFamily)
            .filter { it.transform().position.dst(agentPosition) < agentProps.viewDistance }
            .filter {
                canISeeYouFromHere(
                    agentPosition,
                    agentProps.directionVector,
                    it.transform().position,
                    agentProps.fieldOfView
                )
            }
        debug { "LookForAndStore found ${inrangeEntities.size} entities in range and in the field of view" }
        val seenEntityPositions = mutableListOf<Vector2>()
        for (potential in inrangeEntities) {
            val entityPosition = potential.transform().position
            if (hasLineOfSight(agentPosition, entityPosition, potential)) {
                debug { "LookForAndStore - entity at $entityPosition can be seen" }
                seenEntityPositions.add(entityPosition)
            }
        }
        val storageComponent =
            if (mapper.has(entity)) mapper.get(entity) else engine.createComponent(storageComponentClass.java)
        storageComponent.storage.clear()
        return if (seenEntityPositions.any()) {
            storageComponent.storage.addAll(seenEntityPositions)
            entity.add(storageComponent)
            Status.SUCCEEDED
        } else {
            entity.remove(storageComponentClass.java)
            Status.FAILED
        }
    }

    override fun toString(): String {
        return "Look for Entities w.\n${componentClass.simpleName}"
    }
}