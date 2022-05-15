package ai

import ai.tasks.EntityDecorator
import ai.tasks.invertDecorator
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.*
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed
import com.badlogic.gdx.ai.btree.decorator.Invert
import com.badlogic.gdx.ai.btree.decorator.Repeat
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue
import ecs.components.enemy.AgentProperties
import ecs.systems.enemy.stateBooleanFact
import factories.world
import injection.Context
import ktx.box2d.Query
import ktx.box2d.query
import ktx.math.random
import ktx.math.vec2
import map.grid.Coordinate
import map.grid.GridMapManager
import physics.getEntity
import physics.hasObstacle
import physics.isEntity
import turbofacts.TurboFactsOfTheWorld

fun progressPath(enemyComponent: AgentProperties, currentPosition: Vector2): Boolean {
    if (enemyComponent.needsNewNextPosition && !enemyComponent.path.isEmpty) {
        enemyComponent.nextPosition = enemyComponent.path.removeFirst()
        enemyComponent.needsNewNextPosition = false
        stateBooleanFact(false, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
        Context.inject<TurboFactsOfTheWorld>().setBooleanFact(false, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
    }
    if (currentPosition.dst(enemyComponent.nextPosition) <= 1f) {
        enemyComponent.nextPosition = vec2()
        enemyComponent.needsNewNextPosition = true
        stateBooleanFact(true, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
        Context.inject<TurboFactsOfTheWorld>().setBooleanFact(true, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
    }

    val direction = enemyComponent.nextPosition.cpy().sub(currentPosition).nor()
    enemyComponent.directionVector.set(direction)
    return enemyComponent.path.isEmpty
}

fun findPathFromTo(q: Queue<Vector2>, from: Coordinate, to: Coordinate) {
    q.clear()
    val mapManager = Context.inject<GridMapManager>()
    val path = mapManager.sectionGraph.findPath(from, to)
    for (i in 0 until path.count) {
        val target = path.get(i)
        val section = mapManager.gridMap[target]!!
        val someSpotInThatPlace = section.safePoints.random()
        q.addLast(someSpotInThatPlace)
    }
}

fun findPathFromTo(enemyComponent: AgentProperties, from: Coordinate, to: Coordinate) {
    enemyComponent.path.clear()
    val mapManager = Context.inject<GridMapManager>()
    val path = mapManager.sectionGraph.findPath(from, to)
    for (i in 0 until path.count) {
        val target = path.get(i)
        val section = mapManager.gridMap[target]!!
        val someSpotInThatPlace = section.safePoints.random()// .safeBounds.randomPoint())
        enemyComponent.path.addLast(someSpotInThatPlace)
    }
}

fun avoidObstacles(position: Vector2): Vector2 {
    var obstacles = false
    world().query(position.x, position.y, position.x, position.y) {
        if (it.isEntity() && it.getEntity().hasObstacle()) {
            obstacles = true
            Query.STOP
        }
        Query.CONTINUE
    }
    return if (obstacles) {
        position.x += (-1f..1f).random()
        position.y += (-1f..1f).random()
        avoidObstacles(position)
    } else position
}

/**
 * returns a unit vector aimed at target from
 * [this]
 */
fun Vector2.aimTowards(target: Vector2): Vector2 {
    return target.cpy().sub(this).nor()
}
fun deltaTime(): Float {
    return GdxAI.getTimepiece().deltaTime
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)

object Mutator {
    fun getMutatedTree(tree: BehaviorTree<Entity>): BehaviorTree<Entity> {
        val newTree = BehaviorTree<Entity>()
        if (tree.childCount > 0) {
            newTree.addChild(mutateTask(tree.getChild(0)))
        }
        return newTree
    }

//    private fun mutateBrancher(task: BranchTask<Entity>) : BranchTask<Entity> {
//        val possibleClasses = listOf(Selector::class, com.badlogic.gdx.ai.btree.branch.Sequence::class, RandomSelector::class, RandomSequence::class, Parallel::class, DynamicGuardSelector::class).minus(task::class)
//        val c = possibleClasses.random()
//        val newBranchTask = c.createInstance() as BranchTask<Entity>
//        if(task.guard != null) {
//            newBranchTask.guard = task.guard.cloneTask()
//        }
//        for(index in 0 until task.childCount) {
//            val newChild = mutateTask(task.getChild(index))
//            newBranchTask.addChild(newChild)
//        }
//        return newBranchTask
//    }

    private fun mutateDecorator(task: Decorator<Entity>): Task<Entity> {
        val newTask = when (task) {
            is EntityDecorator -> task.invertDecorator()
            is AlwaysFail -> AlwaysSucceed(mutateTask(task.getChild(0)))
            is AlwaysSucceed -> AlwaysFail(mutateTask(task.getChild(0)))
            is Repeat -> mutateRepeat(task)
            is Invert -> task.getChild(0)
            else -> {
                task
            }
        }
        if (task.guard != null) {
            newTask.guard = task.guard.cloneTask()
        }
        return newTask
    }

    private fun mutateRepeat(task: Repeat<Entity>): Repeat<Entity> {
        task.times = ConstantIntegerDistribution((5..50).random())
        return task
    }

    fun mutateTask(task: Task<Entity>): Task<Entity> {
        return when (task) {
            is BranchTask<Entity> -> task
            is Decorator<Entity> -> mutateDecorator(task)
            is LeafTask<Entity> -> mutateLeaf(task)
            else -> {
                Invert()
            }
        }
    }

    private fun mutateLeaf(task: LeafTask<Entity>): LeafTask<Entity> {
        return task
    }
}
