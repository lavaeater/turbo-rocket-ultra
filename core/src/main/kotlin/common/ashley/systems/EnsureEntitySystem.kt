package common.ashley.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.systems.IntervalSystem

class EnsureEntitySystem(vararg definition: EnsureEntityDef ):EntitySystem() {
    private val definitions = listOf(*definition)

    override fun update(deltaTime: Float) {
        for(def in definitions) {
            def.coolDown -= deltaTime
            if(def.coolDown <= 0f) {
                def.coolDown = def.interval
                val diff = def.numberOfEntities - engine.getEntitiesFor(def.entityFamily).size()
                if (diff > 0) {
                    val numToCreate = if(def.random) (0..diff).random() else diff
                    (0..numToCreate).forEach { _ ->
                        def.creator()
                    }
                }
            }
        }
    }
}