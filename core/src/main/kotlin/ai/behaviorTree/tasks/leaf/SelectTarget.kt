package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.ai.PositionStorageComponent
import components.ai.PositionTarget
import kotlin.reflect.KClass

class SelectTarget<Targets : PositionStorageComponent, TargetStorage : PositionTarget>(
    val targets: KClass<Targets>,
    val targetStorage: KClass<TargetStorage>
) : EntityTask() {
    private val targetsMapper by lazy { ComponentMapper.getFor(targets.java) }
    private val targetMapper by lazy { ComponentMapper.getFor(targetStorage.java) }

    override fun copyTo(task: Task<Entity>?): Task<Entity> = SelectTarget(targets, targetStorage)

    override fun cloneTask(): Task<Entity> {
        val clone = SelectTarget(targets, targetStorage)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }

    override fun execute(): Status {
        if (!targetsMapper.has(entity)) return Status.FAILED
        val targets = targetsMapper.get(entity).storage
        if (targets.isEmpty()) return Status.FAILED
        if(targetMapper.has(entity))
            return Status.FAILED
        //Random or what?
        entity.remove(targetStorage.java)
        val ts = engine.createComponent(targetStorage.java)
        ts.position = targets.random()
        entity.add(ts)
        return Status.SUCCEEDED
    }

    override fun toString(): String {
        return "Select Target"
    }

}