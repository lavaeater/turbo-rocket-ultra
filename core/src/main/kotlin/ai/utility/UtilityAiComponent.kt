package ai.utility

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class UtilityAiComponent : Component, Pool.Poolable {
    private val actions = UtilityAiActions.defaultActions.toMutableList()
    private var currentAction: AiAction? = null

    fun updateAction(entity: Entity) {
        actions.sortByDescending { it.score(entity) }
    }

    fun execute(entity: Entity, deltaTime: Float) {
        val potentialAction = actions.first()// actions.maxByOrNull { it.score(entity) }
        if (currentAction != potentialAction) {
            if (currentAction != null)
            currentAction?.abort(entity)
            currentAction = potentialAction
        }
        currentAction?.act(entity, deltaTime)
    }

    override fun reset() {
        actions.clear()
        actions.addAll(UtilityAiActions.defaultActions)
        currentAction = null
    }

    companion object {
        val mapper = mapperFor<UtilityAiComponent>()
        fun get(entity: Entity): UtilityAiComponent {
            return mapper.get(entity)
        }

        fun has(entity:Entity): Boolean {
            return mapper.has(entity)
        }
    }
}