package ai.tasks.leaf

import ai.pathfinding.TileGraph
import ai.tasks.EntityTask
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.CoordinateStorageComponent
import ecs.systems.sectionX
import ecs.systems.sectionY
import map.grid.Coordinate
import physics.transform
import kotlin.reflect.KClass

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

    override fun toString(): String {
        return "Find a section"
    }
}