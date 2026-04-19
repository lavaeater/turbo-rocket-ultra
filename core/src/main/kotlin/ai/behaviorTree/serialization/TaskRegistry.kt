package ai.behaviorTree.serialization

import ai.behaviorTree.builders.*
import ai.behaviorTree.tasks.EntityHasComponentDecorator
import ai.behaviorTree.tasks.EntityDoesNotHaveComponentDecorator
import ai.behaviorTree.tasks.leaf.*
import ai.behaviorTree.tasks.leaf.boss.*
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.*
import com.badlogic.gdx.ai.btree.decorator.*
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution
import components.ai.*
import components.player.PlayerComponent
import kotlin.reflect.KClass

/**
 * All component classes that can appear in serialized leaf/decorator params.
 * Add new entries here when new AI-facing component classes are introduced.
 */
val componentRegistry: Map<String, KClass<out Component>> = mapOf(
    "PlayerComponent"       to PlayerComponent::class,
    "SeenPlayerPositions"   to SeenPlayerPositions::class,
    "AttackPoint"           to AttackPoint::class,
    "Waypoint"              to Waypoint::class,
    "AmblingEndpoint"       to AmblingEndpoint::class,
    "Path"                  to Path::class,
    "TaskComponent"         to TaskComponent::class
)

fun componentClass(name: String): KClass<out Component> =
    componentRegistry[name] ?: error("Unknown component class '$name' in TaskRegistry")

/** Reconstruct a leaf Task from its type name and string params. */
@Suppress("UNCHECKED_CAST")
fun leafFromSerialized(type: String, params: Map<String, String>): Task<Entity> = when (type) {
    "DelayTask"    -> DelayTask(params["delayFor"]!!.toFloat())
    "RotateTask"   -> RotateTask(
        params["degrees"]!!.toFloat(),
        params["counterClockwise"]?.toBoolean() ?: true
    )
    "AttackTarget" -> AttackTarget(componentClass(params["targetComponentClass"]!!))
    "LookForAndStore" -> LookForAndStore(
        componentClass(params["componentClass"]!!),
        componentClass(params["storageComponentClass"]!!) as KClass<PositionStorageComponent>,
        params["stop"]?.toBoolean() ?: true
    )
    "FindPathTo"   -> FindPathTo(componentClass(params["componentClass"]!!) as KClass<CoordinateStorageComponent>)
    "NextStepOnPath" -> NextStepOnPath()
    "SelectSection" -> SelectSection(
        componentClass(params["componentClass"]!!) as KClass<CoordinateStorageComponent>,
        SectionFindingMethods::classicRandom
    )
    "SelectTarget" -> SelectTarget(
        componentClass(params["targets"]!!) as KClass<PositionStorageComponent>,
        componentClass(params["targetStorage"]!!) as KClass<PositionTarget>
    )
    "MoveTowardsPositionTarget" -> MoveTowardsPositionTarget(
        params["run"]?.toBoolean() ?: false,
        componentClass(params["componentClass"]!!) as KClass<PositionTarget>
    )
    "PlayerIsInGrabRange" -> PlayerIsInGrabRange(params["grabRange"]!!.toFloat())
    "RushPlayer"          -> RushPlayer(params["damage"]?.toFloat() ?: 20f)
    "GrabAndThrowPlayer"  -> GrabAndThrowPlayer(
        params["grabRange"]?.toFloat() ?: 2f,
        params["stunDuration"]?.toFloat() ?: 1.5f,
        params["throwForce"]?.toFloat() ?: 25f,
        params["damage"]?.toFloat() ?: 30f
    )
    "ThrowBottle"   -> ThrowBottle(params["range"]?.toFloat() ?: 15f, params["damage"]?.toFloat() ?: 25f)
    "ThrowSnowball" -> ThrowSnowball(params["range"]?.toFloat() ?: 12f, params["slowDuration"]?.toFloat() ?: 2f)
    "ThrowTarBall"  -> ThrowTarBall(params["range"]?.toFloat() ?: 10f, params["slowDuration"]?.toFloat() ?: 3f)
    "SpinAttack"    -> SpinAttack(params["damage"]?.toFloat() ?: 15f, params["radius"]?.toFloat() ?: 3f)
    "Dash"          -> Dash(params["speed"]?.toFloat() ?: 30f, params["damage"]?.toFloat() ?: 10f)
    "ChargeUpLaser" -> ChargeUpLaser(params["chargeTime"]?.toFloat() ?: 2f, params["damage"]?.toFloat() ?: 50f)
    else -> error("Unknown leaf type '$type'")
}

/** Reconstruct a decorator Task from its type name, child, and params. */
@Suppress("UNCHECKED_CAST")
fun decorFromSerialized(type: String, child: Task<Entity>, params: Map<String, String>): Task<Entity> = when (type) {
    "Invert"        -> Invert(child)
    "AlwaysFail"    -> AlwaysFail(child)
    "AlwaysSucceed" -> AlwaysSucceed(child)
    "Repeat" -> {
        val low = params["low"]?.toInt()
        val high = params["high"]?.toInt()
        val times = params["times"]?.toInt()
        when {
            times == -1 -> Repeat(ConstantIntegerDistribution.NEGATIVE_ONE, child)
            times != null -> Repeat(ConstantIntegerDistribution(times), child)
            low != null && high != null -> Repeat(UniformIntegerDistribution(low, high), child)
            else -> Repeat(ConstantIntegerDistribution.NEGATIVE_ONE, child)
        }
    }
    "HasComponent"         -> EntityHasComponentDecorator(child, componentClass(params["componentClass"]!!).java)
    "DoesNotHaveComponent" -> EntityDoesNotHaveComponentDecorator(child, componentClass(params["componentClass"]!!).java)
    else -> error("Unknown decorator type '$type'")
}

/** Reconstruct a branch Task from its type name and children. */
fun branchFromSerialized(type: String, children: List<Task<Entity>>): Task<Entity> {
    val task: Task<Entity> = when (type) {
        "Selector"            -> Selector()
        "Sequence"            -> com.badlogic.gdx.ai.btree.branch.Sequence()
        "Parallel"            -> Parallel()
        "RandomSelector"      -> RandomSelector()
        "RandomSequence"      -> RandomSequence()
        "DynamicGuardSelector"-> DynamicGuardSelector()
        else -> error("Unknown branch type '$type'")
    }
    children.forEach { task.addChild(it) }
    return task
}
