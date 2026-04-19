package ai.behaviorTree.serialization

import ai.behaviorTree.builders.ComponentExistenceGuard
import ai.behaviorTree.tasks.EntityDoesNotHaveComponentDecorator
import ai.behaviorTree.tasks.EntityHasComponentDecorator
import ai.behaviorTree.tasks.leaf.*
import ai.behaviorTree.tasks.leaf.boss.*
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.BranchTask
import com.badlogic.gdx.ai.btree.Decorator
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.*
import com.badlogic.gdx.ai.btree.decorator.*
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue

object BehaviorTreeSerializer {

    // ──────────────────────────── SERIALIZE ────────────────────────────

    fun serialize(tree: BehaviorTree<Entity>): String {
        if (tree.childCount == 0) return "{}"
        val sb = StringBuilder()
        writeTask(tree.getChild(0), sb)
        return sb.toString()
    }

    private fun writeTask(task: Task<Entity>, sb: StringBuilder) {
        when {
            task is BranchTask -> writeBranch(task, sb)
            task is Decorator  -> writeDecor(task, sb)
            task is LeafTask   -> writeLeaf(task, sb)
            else -> sb.append("{\"t\":\"unknown\"}")
        }
    }

    private fun writeBranch(task: BranchTask<Entity>, sb: StringBuilder) {
        val typeName = when (task) {
            is Selector             -> "Selector"
            is com.badlogic.gdx.ai.btree.branch.Sequence -> "Sequence"
            is Parallel             -> "Parallel"
            is RandomSelector       -> "RandomSelector"
            is RandomSequence       -> "RandomSequence"
            is DynamicGuardSelector -> "DynamicGuardSelector"
            else -> task::class.simpleName ?: "Unknown"
        }
        sb.append("{\"t\":\"branch\",\"bt\":\"$typeName\",\"children\":[")
        for (i in 0 until task.childCount) {
            if (i > 0) sb.append(',')
            writeTask(task.getChild(i), sb)
        }
        sb.append(']')
        task.guard?.let { sb.append(",\"guard\":"); writeTask(it, sb) }
        sb.append('}')
    }

    private fun writeDecor(task: Decorator<Entity>, sb: StringBuilder) {
        val (typeName, params) = when (task) {
            is Invert        -> "Invert" to emptyMap()
            is AlwaysFail    -> "AlwaysFail" to emptyMap()
            is AlwaysSucceed -> "AlwaysSucceed" to emptyMap()
            is Repeat -> {
                val dist = task.times
                val p = when {
                    dist is com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution && dist.value < 0 -> mapOf("times" to "-1")
                    dist is com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution -> mapOf("times" to dist.value.toString())
                    dist is com.badlogic.gdx.ai.utils.random.UniformIntegerDistribution -> mapOf("times" to "-1") // uniform: serialize as infinite for simplicity
                    else -> mapOf("times" to "-1")
                }
                "Repeat" to p
            }
            is EntityHasComponentDecorator<*> -> "HasComponent" to mapOf("componentClass" to task.componentClass.simpleName!!)
            is EntityDoesNotHaveComponentDecorator<*> -> "DoesNotHaveComponent" to mapOf("componentClass" to task.componentClass.simpleName!!)
            else -> (task::class.simpleName ?: "Unknown") to emptyMap()
        }
        val child = if (task.childCount > 0) task.getChild(0) else null
        sb.append("{\"t\":\"decor\",\"dt\":\"$typeName\",\"params\":")
        writeParams(params, sb)
        if (child != null) { sb.append(",\"child\":"); writeTask(child, sb) }
        task.guard?.let { sb.append(",\"guard\":"); writeTask(it, sb) }
        sb.append('}')
    }

    private fun writeLeaf(task: LeafTask<Entity>, sb: StringBuilder) {
        val (typeName, params) = leafParams(task)
        sb.append("{\"t\":\"leaf\",\"lt\":\"$typeName\",\"params\":")
        writeParams(params, sb)
        task.guard?.let { sb.append(",\"guard\":"); writeTask(it, sb) }
        sb.append('}')
    }

    private fun writeParams(params: Map<String, String>, sb: StringBuilder) {
        sb.append('{')
        params.entries.forEachIndexed { i, (k, v) ->
            if (i > 0) sb.append(',')
            sb.append("\"$k\":\"${v.replace("\"", "\\\"")}\"")
        }
        sb.append('}')
    }

    private fun leafParams(task: LeafTask<Entity>): Pair<String, Map<String, String>> = when (task) {
        is DelayTask    -> "DelayTask"    to mapOf("delayFor" to task.delayFor.toString())
        is RotateTask   -> "RotateTask"   to mapOf("degrees" to task.degrees.toString(), "counterClockwise" to task.counterClockwise.toString())
        is AttackTarget<*>     -> "AttackTarget"  to mapOf("targetComponentClass" to task.targetComponentClass.simpleName!!)
        is LookForAndStore<*, *> -> "LookForAndStore" to mapOf(
            "componentClass" to task.componentClass.simpleName!!,
            "storageComponentClass" to task.storageComponentClass.simpleName!!,
            "stop" to task.stop.toString()
        )
        is FindPathTo<*>     -> "FindPathTo"    to mapOf("componentClass" to task.componentClass.simpleName!!)
        is NextStepOnPath    -> "NextStepOnPath" to emptyMap()
        is SelectSection<*>  -> "SelectSection" to mapOf("componentClass" to task.componentClass.simpleName!!)
        is SelectTarget<*, *> -> "SelectTarget" to mapOf(
            "targets" to task.targets.simpleName!!,
            "targetStorage" to task.targetStorage.simpleName!!
        )
        is MoveTowardsPositionTarget<*> -> "MoveTowardsPositionTarget" to mapOf(
            "run" to task.run.toString(),
            "componentClass" to task.componentClass.simpleName!!
        )
        is PlayerIsInGrabRange -> "PlayerIsInGrabRange" to mapOf("grabRange" to task.grabRange.toString())
        is RushPlayer   -> "RushPlayer"   to mapOf("damage" to task.damage.toString())
        is GrabAndThrowPlayer -> "GrabAndThrowPlayer" to mapOf(
            "grabRange" to task.grabRange.toString(),
            "stunDuration" to task.stunDuration.toString(),
            "throwForce" to task.throwForce.toString(),
            "damage" to task.damage.toString()
        )
        is ComponentExistenceGuard<*> -> "ComponentGuard" to mapOf(
            "mustHave" to task.mustHave.toString(),
            "componentClass" to task.componentClass.simpleName!!
        )
        is ThrowBottle   -> "ThrowBottle"   to mapOf("range" to task.range.toString(), "damage" to task.damage.toString())
        is ThrowSnowball -> "ThrowSnowball" to mapOf("range" to task.range.toString(), "slowDuration" to task.slowDuration.toString())
        is ThrowTarBall  -> "ThrowTarBall"  to mapOf("range" to task.range.toString(), "slowDuration" to task.slowDuration.toString())
        is SpinAttack    -> "SpinAttack"    to mapOf("damage" to task.damage.toString(), "radius" to task.radius.toString())
        is Dash          -> "Dash"          to mapOf("speed" to task.speed.toString(), "damage" to task.damage.toString())
        is ChargeUpLaser -> "ChargeUpLaser" to mapOf("chargeTime" to task.chargeTime.toString(), "damage" to task.damage.toString())
        else -> (task::class.simpleName ?: "Unknown") to emptyMap()
    }

    // ──────────────────────────── DESERIALIZE ──────────────────────────

    fun deserialize(json: String): BehaviorTree<Entity> {
        val root = JsonReader().parse(json)
        val tree = BehaviorTree<Entity>()
        tree.addChild(nodeFromJson(root))
        return tree
    }

    private fun nodeFromJson(v: JsonValue): Task<Entity> {
        val type = v.getString("t")
        val guard = v["guard"]?.let { nodeFromJson(it) }
        val task: Task<Entity> = when (type) {
            "branch" -> {
                val children = mutableListOf<Task<Entity>>()
                val childrenJson = v["children"]
                if (childrenJson != null)
                    for (i in 0 until childrenJson.size) children.add(nodeFromJson(childrenJson[i]))
                branchFromSerialized(v.getString("bt"), children)
            }
            "decor" -> {
                val child = v["child"]?.let { nodeFromJson(it) } ?: error("Decorator missing child")
                val params = paramsFromJson(v["params"])
                decorFromSerialized(v.getString("dt"), child, params)
            }
            "leaf" -> {
                val params = paramsFromJson(v["params"])
                leafFromSerialized(v.getString("lt"), params)
            }
            else -> error("Unknown task type '$type'")
        }
        task.guard = guard
        return task
    }

    private fun paramsFromJson(v: JsonValue?): Map<String, String> {
        if (v == null) return emptyMap()
        val map = mutableMapOf<String, String>()
        var child = v.child
        while (child != null) {
            map[child.name] = child.asString()
            child = child.next
        }
        return map
    }

    // ──────────────────────────── FILE I/O ─────────────────────────────

    fun saveToFile(tree: BehaviorTree<Entity>, path: String) {
        com.badlogic.gdx.Gdx.files.local(path).writeString(serialize(tree), false)
    }

    fun loadFromFile(path: String): BehaviorTree<Entity> {
        return deserialize(com.badlogic.gdx.Gdx.files.local(path).readString())
    }
}
