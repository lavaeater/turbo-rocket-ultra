package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import kotlin.reflect.KType

class Memory : Component, Pool.Poolable {
    val seenEntities = mutableMapOf<KType, MutableMap<Entity, Float>>()
    val closeEntities = mutableMapOf<KType, MutableMap<Entity, Float>>()
    val generalMemory = mutableMapOf<GeneralMemory, Float>()
    val timePiece by lazy { GdxAI.getTimepiece() }

    var lastCheckTime = 0f
    var lastUpdateTime = 0f

    fun addGeneralMemory(memory: GeneralMemory) {
        lastUpdateTime = timePiece.time
        generalMemory[memory] = memoryLifeSpan// memoryLifeSpanRange.random().toFloat()
    }

    fun updateGeneralMemories(deltaTime: Float) {
        val generalToRemove = mutableListOf<GeneralMemory>()
        for((key, value) in generalMemory) {
            generalMemory[key] = value - deltaTime
            if(generalMemory[key]!! <= 0f) {
                generalToRemove.add(key)
            }
        }
        for(t in generalToRemove) {
            generalMemory.remove(t)
            lastUpdateTime = timePiece.time
        }
    }

    val hasGeneralMemoryChanged : Boolean
        get() {
        return if(lastUpdateTime > lastCheckTime) {
            lastCheckTime = timePiece.time
            true
        } else {
            false
        }
    }


    val memoryLifeSpan = 5f
    val memoryLifeSpanRange = 1..10
    override fun reset() {
        seenEntities.clear()
        closeEntities.clear()
        generalMemory.clear()
    }

    companion object {
        val mapper = mapperFor<Memory>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): Memory {
            return mapper.get(entity)
        }
    }
}