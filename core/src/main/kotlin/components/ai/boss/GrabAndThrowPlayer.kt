package components.ai.boss

import components.ai.TaskComponent

class GrabAndThrowPlayer: TaskComponent() {
    init {
        coolDownRange = (.1f..0.5f)
        coolDown = 0f
    }
    override fun toString(): String {
        return "grabthrow"
    }

}