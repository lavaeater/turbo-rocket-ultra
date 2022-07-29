package ai.builders

import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector

class DynamicGuardSelectorBuilder<T>: CompositeTaskBuilder<T>() {

    fun doThis(task: Task<T>) : Task<T> {
        tasks.add(task)
        return task
    }
    fun ifThis(task: Task<T>) : Task<T> {
        tasks.add(task)
        return task
    }
    override fun build(): DynamicGuardSelector<T> = DynamicGuardSelector(*tasks.toTypedArray())
}