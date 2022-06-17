package ecs.systems.ai.utility

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class UtilityAiComponent : Component, Pool.Poolable {
    val actions = defaultActions.toMutableList()
    private var currentAction: AiAction? = null
    fun topAction(entity: Entity): AiAction? {
        val potentialAction = actions.minByOrNull { it.score(entity) }
        if (currentAction != potentialAction) {
            currentAction = potentialAction
        }
        return currentAction
    }

    override fun reset() {
        actions.clear()
        actions.addAll(defaultActions)
    }

    companion object {
        val defaultActions = setOf(AmbleAiAction())
    }
}