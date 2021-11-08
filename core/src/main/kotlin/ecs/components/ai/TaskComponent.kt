package ecs.components.ai

import com.badlogic.gdx.ai.btree.Task

abstract class TaskComponent : CoolDownComponent() {
    var status : Task.Status = Task.Status.RUNNING
    var needsInit = true
    override fun reset() {
        super.reset()
        needsInit = true
        status = Task.Status.RUNNING
    }
}