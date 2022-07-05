package ai

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.branch.DynamicGuardSelector
import com.badlogic.gdx.ai.btree.branch.Parallel
import com.badlogic.gdx.ai.btree.branch.Selector
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed
import com.badlogic.gdx.ai.btree.decorator.Invert
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue
import eater.ecs.components.AgentProperties
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
import eater.turbofacts.TurboFactsOfTheWorld

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

fun <T> Task<T>.treeString(): String {
    return when (this) {
        is BehaviorTree<T> -> "BehaviorTree"
        is Selector<T> -> "Selector"
        is com.badlogic.gdx.ai.btree.branch.Sequence<T> -> "Sequence"
        is Parallel<T> -> "Parallel"
        is DynamicGuardSelector<T> -> "Dynamic"
        is Invert<T> -> "Invert result of"
        is AlwaysFail<T> -> "Always Fail"
        is AlwaysSucceed<T> -> "Always Succeed"
        else -> toString()
    }
}

