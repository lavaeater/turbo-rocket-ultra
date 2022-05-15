package ai.builders

import ai.tasks.EntityTask
import ai.tasks.leaf.*
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.decorator.*
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution
import ecs.components.ai.CoordinateStorageComponent
import ecs.components.ai.PositionStorageComponent
import ecs.components.ai.old.TaskComponent
import ktx.log.debug
import ktx.log.info
import map.grid.Coordinate
import kotlin.reflect.KClass

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
fun <T> runUntilFirstSucceeds(block: SelectorBuilder<T>.() -> Unit) = SelectorBuilder<T>().apply(block).build()
fun <T> runInTurnUntilFirstFailure(block: SequenceBuilder<T>.() -> Unit) = SequenceBuilder<T>().apply(block).build()
fun <T> runInSequence(block: SequenceBuilder<T>.() -> Unit) = SequenceBuilder<T>().apply(block).build()
fun <T> parallel(block: ParallelBuilder<T>.() -> Unit) = ParallelBuilder<T>().apply(block).build()

inline fun <reified T : TaskComponent> entityDo(block: EntityComponentTaskBuilder<T>.() -> Unit = {}) =
    EntityComponentTaskBuilder(T::class.java).apply(block).build()

inline fun <reified ToLookFor : Component, reified ToStoreIn : PositionStorageComponent> lookForAndStore(): LookForAndStore<ToLookFor, ToStoreIn> {
    return LookForAndStore(ToLookFor::class, ToStoreIn::class)
}

inline fun <reified ToStoreIn : CoordinateStorageComponent> findSection(
    noinline method: (Coordinate, Int, Int) -> Coordinate = SectionFindingMethods::classicRandom
): FindSection<ToStoreIn> {
    return FindSection(ToStoreIn::class, method)
}

fun getNextStepOnPath(): NextStepOnPath {
    return NextStepOnPath()
}

fun moveTowardsPositionTarget(run: Boolean = false): MoveTowardsPositionTarget {
    return MoveTowardsPositionTarget(run)
}

inline fun <reified Storage : CoordinateStorageComponent> findPathTo(): FindPathTo<Storage> {
    return FindPathTo(Storage::class)
}
//
//inline fun <reified T : Component> ifEntityHasNot(
//    task: Task<Entity>,
//    block: EntityDoesNotHaveComponentGuardBuilder<T>.() -> Unit = {}
//) = EntityDoesNotHaveComponentGuardBuilder(task, T::class.java).apply(block).build()
//
//inline fun <reified T : Component> ifEntityHas(
//    task: Task<Entity>,
//    block: EntityHasComponentGuardBuilder<T>.() -> Unit = {}
//) = EntityHasComponentGuardBuilder(task, T::class.java).apply(block).build()

inline fun <reified T : Component> onlyIfEntityHas(task: Task<Entity>): Task<Entity> {
    task.guard = ComponentExistenceGuard(true, T::class)
    return task
}

inline fun <reified T : Component> onlyIfEntityDoesNotHave(task: Task<Entity>): Task<Entity> {
    task.guard = ComponentExistenceGuard(false, T::class)
    return task
}

class ComponentExistenceGuard<T : Component>(private val mustHave: Boolean, private val componentClass: KClass<T>) :
    EntityTask() {
    @delegate: Transient
    private val mapper by lazy { ComponentMapper.getFor(componentClass.java) }
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        return if (mustHave) {
            if (mapper.has(entity)) {
                debug { "Entity has ${componentClass.simpleName} and this is good" }
                Status.SUCCEEDED
            } else {
                debug { "Entity does not have ${componentClass.simpleName} and this is bad" }
                Status.FAILED
            }
        } else {
            if (mapper.has(entity)) {
                debug { "Entity has ${componentClass.simpleName} and this is bad" }
                Status.FAILED
            } else {
                debug { "Entity does not have ${componentClass.simpleName} and this is good" }
                Status.SUCCEEDED
            }
        }
    }

    override fun toString(): String {
        return if (mustHave)
            "Entity must have ${componentClass.simpleName}"
        else
            "Entity must NOT have ${componentClass.simpleName}"
    }
}