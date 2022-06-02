package ai.builders

import ai.tasks.leaf.*
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.decorator.*
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution
import ecs.components.ai.*
import ecs.components.ai.TaskComponent
import map.grid.Coordinate

fun delayFor(seconds: Float) = DelayTask(seconds)
fun rotate(degrees: Float, counterClockwise: Boolean = true) = RotateTask(degrees, counterClockwise)
fun <T> semaphoreGuard(task: Task<T>) = SemaphoreGuard(task)
fun <T> repeat(times: Int, task: Task<T>) = Repeat(ConstantIntegerDistribution(times), task)
fun <T> repeat(low: Int, high: Int, task: Task<T>) = Repeat(UniformIntegerDistribution(low, high), task)
fun <T> repeatForever(task: Task<T>) = Repeat(ConstantIntegerDistribution.NEGATIVE_ONE, task)
fun <T> include(tree: String) = Include<T>(tree)
fun <T> succeed(task: Task<T>) = AlwaysSucceed(task)
fun <T> fail(task: Task<T>) = AlwaysFail(task)
fun <T> invertResultOf(task: Task<T>) = Invert(task)
fun <T> tree(block: TreeBuilder<T>.() -> Unit) = TreeBuilder<T>().apply(block).build()
fun <T> exitOnFirstThatSucceeds(block: SelectorBuilder<T>.() -> Unit) = SelectorBuilder<T>().apply(block).build()
fun <T> selector(block: SelectorBuilder<T>.() -> Unit) = SelectorBuilder<T>().apply(block).build()
fun <T> tryInTurn(block: SelectorBuilder<T>.() -> Unit) = SelectorBuilder<T>().apply(block).build()
fun <T> dyanmicGuardSelector(block: DynamicGuardSelectorBuilder<T>.() -> Unit) =
    DynamicGuardSelectorBuilder<T>().apply(block).build()

fun <T> runInTurnUntilFirstFailure(block: SequenceBuilder<T>.() -> Unit) = SequenceBuilder<T>().apply(block).build()
fun <T> exitOnFirstThatFails(block: SequenceBuilder<T>.() -> Unit) = SequenceBuilder<T>().apply(block).build()
fun <T> parallel(block: ParallelBuilder<T>.() -> Unit) = ParallelBuilder<T>().apply(block).build()

inline fun <reified T : TaskComponent> entityDo(block: EntityComponentTaskBuilder<T>.() -> Unit = {}) =
    EntityComponentTaskBuilder(T::class.java).apply(block).build()

inline fun <reified ToLookFor : Component, reified ToStoreIn : PositionStorageComponent> lookForAndStore(stop: Boolean): LookForAndStore<ToLookFor, ToStoreIn> {
    return LookForAndStore(ToLookFor::class, ToStoreIn::class, stop)
}

inline fun <reified Targets : PositionStorageComponent,
        reified TargetStorage : PositionTarget> selectTarget(): SelectTarget<Targets, TargetStorage> {
    return SelectTarget(Targets::class, TargetStorage::class)
}

inline fun <reified ToStoreIn : CoordinateStorageComponent> findSection(
    noinline method: (Coordinate, Int, Int) -> Coordinate = SectionFindingMethods::classicRandom
): SelectSection<ToStoreIn> {
    return SelectSection(ToStoreIn::class, method)
}

fun getNextStepOnPath(): NextStepOnPath {
    return NextStepOnPath()
}

inline fun <reified T : PositionTarget> moveTowardsPositionTarget(run: Boolean = false): MoveTowardsPositionTarget<T> {
    return MoveTowardsPositionTarget(run, T::class)
}

inline fun <reified T: Component> attack(): AttackTarget<T> {
    return AttackTarget(T::class)
}

inline fun <reified Storage : CoordinateStorageComponent> findPathTo(): FindPathTo<Storage> {
    return FindPathTo(Storage::class)
}

inline fun <reified T : Component> onlyIfEntityHas(task: Task<Entity>): Task<Entity> {
    task.guard = ComponentExistenceGuard(true, T::class)
    return task
}

inline fun <reified T : Component> entityHas(): Task<Entity> {
    return ComponentExistenceGuard(true, T::class)
}

inline fun <reified T : Component> entityDoesNotHave(): Task<Entity> {
    return ComponentExistenceGuard(false, T::class)
}

inline fun <reified T : Component> onlyIfEntityDoesNotHave(task: Task<Entity>): Task<Entity> {
    task.guard = ComponentExistenceGuard(false, T::class)
    return task
}

