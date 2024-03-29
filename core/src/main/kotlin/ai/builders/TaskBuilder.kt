package ai.builders

import ai.BehaviorTreeMarker
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import tru.Builder

@BehaviorTreeMarker
abstract class TaskBuilder<T>: Builder<Task<T>> {
    abstract fun add(task: Task<T>)
//    fun dynamicGuardSelector(block: DynamicGuardSelectorBuilder<T>.() -> Unit) = add(DynamicGuardSelectorBuilder<T>().apply(block).build())
//    fun sequence(block: SequenceBuilder<T>.() -> Unit): Task<T> = add(ai.builders.sequence(block))
//    fun selector(block: SelectorBuilder<T>.() -> Unit): Task<T> = add(ai.builders.selector(block))
//    fun parallel(block: ParallelBuilder<T>.() -> Unit) = add(ai.builders.parallel(block))
//    fun randomSelector(block: RandomSelectorBuilder<T>.() -> Unit) = RandomSelectorBuilder<T>().apply(block).build()
//    fun randomSequence(block: RandomSequenceBuilder<T>.() -> Unit) = RandomSequenceBuilder<T>().apply(block).build()
}

