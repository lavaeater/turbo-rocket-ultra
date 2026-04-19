package ai.behaviorTree.tasks.leaf

import ai.behaviorTree.tasks.EntityTask
import ai.pathfinding.TileGraph
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.Task
import components.ai.CoordinateStorageComponent
import systems.sectionX
import systems.sectionY
import ktx.log.debug
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
class SelectSection<T: CoordinateStorageComponent>(val componentClass: KClass<T>, val method: (Coordinate, Int, Int) -> Coordinate?) : EntityTask() {
    override fun copyTo(task: Task<Entity>?): Task<Entity> = SelectSection(componentClass, method)

    override fun cloneTask(): Task<Entity> {
        val clone = SelectSection(componentClass, method)
        if (guard != null) clone.guard = guard.cloneTask()
        return clone
    }

    override fun execute(): Status {
        val position = entity.transform().position
        val currentSection = TileGraph.getCoordinateInstance(position.sectionX(), position.sectionY())
        val foundSection = method(currentSection, 3, 5) //must it be able to fail? - yes
        entity.remove(componentClass.java)
        if(foundSection != null) {
            entity.add(engine.createComponent(componentClass.java).apply {
                storage.add(currentSection)
                storage.add(foundSection)
            })
            debug { "Found section $foundSection" }
            return Status.SUCCEEDED
        }
        return Status.FAILED
    }

    override fun toString(): String {
        return "Select Section"
    }
}