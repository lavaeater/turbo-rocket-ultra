package ai.tasks.leaf

import ai.findPathFromTo
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.CoordinateStorageComponent
import ecs.components.ai.Path
import ktx.ashley.remove
import eater.physics.addComponent
import kotlin.reflect.KClass

class FindPathTo<T: CoordinateStorageComponent>(private val componentClass: KClass<T>) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        entity.remove<Path>()
        val coordStorage = entity.getComponent(componentClass.java)
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