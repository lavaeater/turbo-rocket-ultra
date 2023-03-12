package eater.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import eater.ecs.ashley.components.GeneralMemory
import eater.ecs.ashley.components.Memory
import ktx.ashley.allOf
import kotlin.reflect.KType

class UpdateMemorySystem : IteratingSystem(allOf(Memory::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val memory = Memory.get(entity)
        val toRemove = mutableMapOf<KType, MutableList<Entity>>()
        for ((t, map) in memory.closeEntities) {
            for ((key, value) in map) {
                map[key] = value - deltaTime
                if (map[key]!! < 0f || !engine.entities.contains(key)) {
                    if (!toRemove.containsKey(t))
                        toRemove[t] = mutableListOf()
                    toRemove[t]!!.add(key)
                }
            }
        }
        for ((t, e) in toRemove) {
            e.forEach { memory.closeEntities[t]?.remove(it) }
        }
        toRemove.clear()
        for ((t, map) in memory.seenEntities) {
            for ((key, value) in map) {
                map[key] = value - deltaTime
                if (map[key]!! < 0f || !engine.entities.contains(key)) {
                    if (!toRemove.containsKey(t))
                        toRemove[t] = mutableListOf()
                    toRemove[t]!!.add(key)
                }
            }
        }
        for ((t, e) in toRemove) {
            e.forEach { memory.seenEntities[t]?.remove(it) }
        }
        toRemove.clear()

        memory.updateGeneralMemories(deltaTime)
    }
}