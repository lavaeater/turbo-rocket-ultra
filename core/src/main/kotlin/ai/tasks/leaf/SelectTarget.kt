package ai.tasks.leaf

import ai.tasks.EntityTask
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.PositionStorageComponent
import ecs.components.ai.PositionTarget
import physics.addComponent
import kotlin.reflect.KClass

class SelectTarget<Targets : PositionStorageComponent, TargetStorage : PositionTarget>(
    targets: KClass<Targets>,
    private val targetStorage: KClass<TargetStorage>
) : EntityTask() {
    private val targetsMapper by lazy { ComponentMapper.getFor(targets.java) }
    private val targetMapper by lazy { ComponentMapper.getFor(targetStorage.java) }

    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
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

}