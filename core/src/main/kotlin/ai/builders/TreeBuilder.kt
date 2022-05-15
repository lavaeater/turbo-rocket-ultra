package ai.builders

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task

class TreeBuilder<T> : TaskBuilder<T>() {
    private lateinit var rootTask: Task<T>

    override fun build(): BehaviorTree<T> = BehaviorTree(rootTask)
    override fun add(task: Task<T>) {
        rootTask = task
    }

    fun root(rootTask:Task<T>) {
        add(rootTask)
    }
}