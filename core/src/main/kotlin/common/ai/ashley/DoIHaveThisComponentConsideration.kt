package common.ai.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import kotlin.reflect.KClass

class DoIHaveThisComponentConsideration<ToCheck: Component>(toCheck: KClass<ToCheck>, name:String = "Check for a component"): Consideration(name) {
    val mapper = ComponentMapper.getFor(toCheck.java)
    override fun normalizedScore(entity: Entity): Float {
        return if(mapper.has(entity)) 1.0f else 0f
    }
}