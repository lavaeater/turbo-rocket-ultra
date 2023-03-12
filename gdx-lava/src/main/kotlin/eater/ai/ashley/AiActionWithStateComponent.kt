package eater.ai.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import eater.core.engine
import ktx.log.info
import kotlin.reflect.KClass

abstract class AiActionWithStateComponent<T: Component>(name: String, private val stateComponentClass: KClass<T>): AiAction(name) {
    val mapper = ComponentMapper.getFor(stateComponentClass.java)!!
    private fun removeState(entity: Entity) {
        entity.remove(stateComponentClass.java)
    }

    private fun ensureState(entity: Entity): T {
        if(!mapper.has(entity)) {
            val stateComponent = engine().createComponent(stateComponentClass.java)
            entity.add(stateComponent)
            initState(stateComponent)
        }
        return entity.getComponent(stateComponentClass.java)
    }

    open fun initState(stateComponent: T) {
        //Default is of course no-op
    }

    abstract fun scoreFunction(entity: Entity):Float

    abstract fun abortFunction(entity: Entity)
    abstract fun actFunction(entity: Entity, stateComponent: T, deltaTime: Float) : Boolean

    override fun abort(entity: Entity) {
        removeState(entity)
        abortFunction(entity)
    }

    override fun act(entity: Entity, deltaTime: Float) :Boolean {
        return actFunction(entity, ensureState(entity), deltaTime)
    }

    override fun updateScore(entity: Entity): Float {
        score = scoreFunction(entity)
        return score
    }
}