package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import eater.ai.ashley.AiAction
import ktx.ashley.mapperFor

class AiComponent : Component, Pool.Poolable {
    val actions = mutableListOf<AiAction>()
    private var currentAction: AiAction? = null
    private var canSwitchAction = true

    fun updateAction(entity: Entity) {
        canSwitchAction = false
        actions.sortByDescending { it.updateScore(entity) }
        canSwitchAction = true
    }

    fun topAction(entity: Entity): AiAction? {
        if(canSwitchAction) {
            val potentialAction = actions.first()
            if (currentAction != potentialAction) {
                currentAction?.abort(entity)
                currentAction = potentialAction
            }
        }
        return currentAction
    }

    override fun reset() {
        actions.clear()
        currentAction = null
    }

    companion object {
        val mapper = mapperFor<AiComponent>()
        fun get(entity: Entity): AiComponent {
            return mapper.get(entity)
        }
        fun has(entity:Entity): Boolean {
            return mapper.has(entity)
        }
    }
}
