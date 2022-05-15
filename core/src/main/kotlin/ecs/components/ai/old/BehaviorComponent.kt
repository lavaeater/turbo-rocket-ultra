package ecs.components.ai.old

import ai.tasks.EntityComponentTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.utils.Pool

class BehaviorComponent : Component,Pool.Poolable {

    var tree: BehaviorTree<Entity> = BehaviorTree<Entity>()
    set(value) {
        field = value
        addListeners()
    }

    var currentStatus = ""

    private fun addListeners() {
        tree.addListener(object: BehaviorTree.Listener<Entity> {
            override fun statusUpdated(task: Task<Entity>, previousStatus: Task.Status) {
                if(task is EntityComponentTask<*> && task.toString() != currentStatus) {
                    currentStatus = task.toString()
                }
            }

            override fun childAdded(task: Task<Entity>?, index: Int) {
                //NO-OP, by design
            }

        })
    }

    override fun reset() {
        tree.removeListeners()
        currentStatus = ""
    }

    override fun toString(): String {
        return currentStatus
    }
}