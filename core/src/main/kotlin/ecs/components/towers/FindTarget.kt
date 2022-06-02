package ecs.components.towers

import ecs.components.ai.TaskComponent

class FindTarget(var radius: Float = 20f): TaskComponent(){
    override fun reset() {
        super.reset()
        radius = 20f
    }

    override fun toString(): String {
        return "find target"
    }
}