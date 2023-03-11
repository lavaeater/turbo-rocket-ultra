package common.ai.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import eater.core.engine
import kotlin.reflect.KClass

class ConsideredActionWithState<T : Component>(
    name: String,
    private val actFunction: (entity: Entity, stateComponent: T, deltaTime: Float) -> Boolean,
    private val stateComponentClass: KClass<T>,
    scoreRange: ClosedFloatingPointRange<Float> = 0f..1f,
    vararg consideration: Consideration
) : AiAction(name, scoreRange) {
    init {
        considerations.addAll(consideration)
    }

    val mapper = ComponentMapper.getFor(stateComponentClass.java)!!
    var abortFunction: (entity: Entity) -> Unit = {}
    var initStateFunction: (T)->Unit = {}

    private fun ensureState(entity: Entity): T {
        if(!mapper.has(entity)) {
            val stateComponent = engine().createComponent(stateComponentClass.java)
            entity.add(stateComponent)
            initState(stateComponent)
        }
        return entity.getComponent(stateComponentClass.java)
    }
    private fun initState(stateComponent: T) {
        initStateFunction(stateComponent)
    }


    private fun discardState(entity: Entity) {
        entity.remove(stateComponentClass.java)
    }

    override fun abort(entity: Entity) {
        discardState(entity)
        abortFunction(entity)
    }

    override fun act(entity: Entity, deltaTime: Float) : Boolean {
        return actFunction(entity, ensureState(entity), deltaTime)
    }
}