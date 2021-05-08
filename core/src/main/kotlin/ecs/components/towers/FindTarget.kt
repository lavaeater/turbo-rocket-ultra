package ecs.components.towers

import ecs.components.ai.TaskComponent

class FindTarget(var radius: Float = 10f): TaskComponent(){
    override fun reset() {
        super.reset()
        radius = 10f
    }
}