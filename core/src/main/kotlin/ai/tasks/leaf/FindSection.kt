package ai.tasks.leaf

import ai.pathfinding.TileGraph
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Queue
import ecs.systems.ai.findPathFromTo
import ecs.systems.sectionX
import ecs.systems.sectionY
import injection.Context.inject
import ktx.ashley.addComponent
import ktx.ashley.remove
import map.grid.Coordinate
import map.grid.GridMapManager
import physics.agentProps
import physics.transform
import kotlin.reflect.KClass

class AmblingEndpointComponent: CoordinateStorageComponent()

open class CoordinateStorageComponent: StorageComponent<Coordinate>()

class PathComponent: QueueComponent<Vector2>()

open class QueueComponent<T>: Component, Pool.Poolable {
    val queue = Queue<T>()
    override fun reset() {
        queue.clear()
    }
}

object SectionFindingMethods {
    fun classicRandom(origin: Coordinate, maxDistance: Int): Coordinate {
        //1. Randomly select a section to move to
        return inject<GridMapManager>().getRandomSection(origin, maxDistance)
    }
}

class FindPathTo<T: CoordinateStorageComponent>(private val componentClass: KClass<T>) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        entity.remove<PathComponent>()
        val coordStorage = entity.getComponent(componentClass.java)
        if(coordStorage.storage.size != 2) return Status.FAILED
        val pathComponent = entity.addComponent<PathComponent>(engine)
        val from = coordStorage.storage.removeFirst() //Remove starting section
        val to = coordStorage.storage.first() //Keep the other one, might need it, might not

        findPathFromTo(pathComponent.queue, from, to)
        return Status.SUCCEEDED
    }

}

/**
 * Should be able to use different methods for
 * finding tiles, just to have fun with exploring different
 * algorithms.
 *
 * The most basic one is "pick a random section"
 */
class FindSection<T: CoordinateStorageComponent>(private val componentClass: KClass<T>, private val method: (Coordinate, Int) -> Coordinate) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> {
        TODO("Not yet implemented")
    }

    override fun execute(): Status {
        val agentProps = entity.agentProps()
        val position = entity.transform().position
        val currentSection = TileGraph.getCoordinateInstance(position.sectionX(), position.sectionY())
        val foundSection = method(currentSection, 10) //must it be able to fail?
        entity.remove(componentClass.java)
        entity.add(engine.createComponent(componentClass.java).apply {
            storage.add(currentSection)
            storage.add(foundSection)
        })
        return Status.SUCCEEDED
    }
}