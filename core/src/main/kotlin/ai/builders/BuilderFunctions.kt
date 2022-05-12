package ai.builders

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
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

inline fun <reified T : Component> entityHas() = HasComponentBuilder(T::class.java).build()

inline fun <reified T: TaskComponent> entityDo(block: EntityComponentTaskBuilder<T>.() -> Unit = {}) = EntityComponentTaskBuilder(T::class.java).apply(block).build()

inline fun <reified T: Component>unlessEntityHas(
    task: Task<Entity>,
    block: EntityDoesNotHaveComponentGuardBuilder<T>.()-> Unit = {}) = EntityDoesNotHaveComponentGuardBuilder(task, T::class.java).apply(block).build()
inline fun <reified T: Component>ifEntityHas
            (task: Task<Entity>,
             block: EntityHasComponentGuardBuilder<T>.()-> Unit = {}) = EntityHasComponentGuardBuilder(task, T::class.java).apply(block).build()
