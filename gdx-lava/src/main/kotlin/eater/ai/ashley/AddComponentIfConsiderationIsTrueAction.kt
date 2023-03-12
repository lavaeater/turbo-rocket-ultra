package eater.ai.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import eater.core.engine
import kotlin.reflect.KClass

/**
 * This action will, in the act function, add a component
 * to the agent. if the agent already has this component
 * the score function will return 0f.
 * However, if the terms for the score are no longer upheld,
 * the component shall be removed.
 *
 * Something like that.
 */
class AddComponentIfConsiderationIsTrueAction<ToAdd: Component>(name: String, scoreRange: ClosedFloatingPointRange<Float>, private val componentToAdd: KClass<ToAdd>, vararg consideration: Consideration) : AiAction(name, scoreRange) {
    init {
        considerations.addAll(consideration)
    }
    val mapper = ComponentMapper.getFor(componentToAdd.java)!!
    var abortFunction: (entity: Entity) -> Unit = {}
    var initStateFunction: (ToAdd)->Unit = {}
    private fun ensureComponent(entity: Entity): ToAdd {
        if(!mapper.has(entity)) {
            val stateComponent = engine().createComponent(componentToAdd.java)
            entity.add(stateComponent)
        }
        return entity.getComponent(componentToAdd.java)
    }

    private fun discardComponent(entity: Entity) {
        entity.remove(componentToAdd.java)
    }

    override fun abort(entity: Entity) {
        discardComponent(entity)
        abortFunction(entity)
    }

    override fun act(entity: Entity, deltaTime: Float) : Boolean {
        /**
         * The motherfucking can i see this consideration adds seen entitities
         * to the motherfucking memory of the entity which is so fudging cool it isn't even cool.
         * No, it is cool, actually.
         *
         * But it uses agent properties, which means I have two sets of properties now. I have
         * to merge this into one component.
         */
        ensureComponent(entity)
        return false // could be useful, but not as we have done it now.
    }
}