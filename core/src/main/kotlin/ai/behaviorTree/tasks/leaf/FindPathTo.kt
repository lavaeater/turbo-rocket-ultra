package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.findPathFromTo
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import physics.addComponent
import components.ai.CoordinateStorageComponent
import components.ai.Path
import ktx.ashley.remove
import kotlin.reflect.KClass

class FindPathTo<T: CoordinateStorageComponent>(val componentClass: KClass<T>) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        return FindPathTo(componentClass)
    }
    override fun cloneTask(): Task<Entity> {
        val clone = FindPathTo(componentClass)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }


    override fun execute(): Status {
        entity.remove<Path>()
        val coordStorage = entity.getComponent(componentClass.java) ?: return Status.FAILED
        if(coordStorage.storage.size != 2) return Status.FAILED
        entity.addComponent<Path> {
            val from = coordStorage.storage.removeFirst() //Remove starting section
            val to = coordStorage.storage.first() //Keep the other one, might need it, might not
            findPathFromTo(this.queue, from, to)
        }
        return Status.SUCCEEDED
    }

    override fun toString(): String {
        return "Find Path To"
    }
}