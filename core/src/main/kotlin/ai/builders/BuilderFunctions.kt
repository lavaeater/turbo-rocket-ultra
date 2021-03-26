package ai.builders

import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.decorator.*
import com.badlogic.gdx.ai.utils.random.IntegerDistribution
import ecs.components.ai.TaskComponent

fun <T>semaphoreGuard(task: Task<T>) = SemaphoreGuard(task)
fun <T>repeat( times: IntegerDistribution, task: Task<T>) = Repeat(times, task)
fun <T>include(tree: String) = Include<T>(tree)
fun <T>succeed(task: Task<T>) = AlwaysSucceed(task)
fun <T>fail(task: Task<T>) = AlwaysFail(task)
fun <T>invert(task: Task<T>) = Invert(task)
fun <T> tree(block: TreeBuilder<T>.() -> Unit) = TreeBuilder<T>().apply(block).build()
fun <T> selector(block: SelectorBuilder<T>.() -> Unit) = SelectorBuilder<T>().apply(block).build()
fun <T> sequence(block: SequenceBuilder<T>.() -> Unit) = SequenceBuilder<T>().apply(block).build()
fun <T> parallel(block: ParallelBuilder<T>.() -> Unit) = ParallelBuilder<T>().apply(block).build()

inline fun <reified T : TaskComponent> isEntity() = HasComponentBuilder(T::class.java).build()
inline fun <reified T : TaskComponent> start() = AddComponentBuilder(T::class.java).build()
inline fun <reified T : TaskComponent> stop() = RemoveComponentBuilder(T::class.java).build()
inline fun <reified T: TaskComponent> execute() = CheckComponentStatusBuilder(T::class.java).build()

inline fun <reified T: TaskComponent> entityDo() = EntityComponentTaskBuilder(T::class.java).build()

