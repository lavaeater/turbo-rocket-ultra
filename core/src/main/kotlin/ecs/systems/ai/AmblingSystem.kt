package ecs.systems.ai

import ai.pathfinding.TileGraph
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Queue
import ecs.components.ai.old.Amble
import ecs.components.ai.old.CollidedWithObstacle
import ecs.components.enemy.AgentProperties
import ecs.components.gameplay.TransformComponent
import ecs.systems.enemy.stateBooleanFact
import ecs.systems.sectionX
import ecs.systems.sectionY
import factories.world
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.remove
import ktx.box2d.Query
import ktx.box2d.query
import ktx.math.random
import ktx.math.vec2
import map.grid.Coordinate
import map.grid.GridMapManager
import physics.*
import turbofacts.TurboFactsOfTheWorld


class AmblingSystem : IteratingSystem(allOf(Amble::class, AgentProperties::class, TransformComponent::class).get()) {

    private val mapManager by lazy { inject<GridMapManager>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = AshleyMappers.amble.get(entity)
        val enemyComponent = entity.agentProps()
        val currentPosition = entity.transform().position

        if (component.firstRun || entity.hasCollidedWithObstacle()) {

            val currentSection = TileGraph.getCoordinateInstance(currentPosition.sectionX(), currentPosition.sectionY())
            //1. Randomly select a section to move to
            val maxDistance = 10
            val randomSection = mapManager.getRandomSection(currentSection, maxDistance)
            //2. Pathfind a path to it
            findPathFromTo(enemyComponent, currentSection, randomSection)

            component.firstRun = false
            if (entity.hasCollidedWithObstacle())
                entity.remove<CollidedWithObstacle>()


        }

        val weAreDone = progressPath(enemyComponent, currentPosition)

        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f) {
                component.status = Task.Status.SUCCEEDED
                entity.playRandomAudioFor("zombie", "groan")
            }
            if (weAreDone) {
                component.status = Task.Status.SUCCEEDED
                entity.playRandomAudioFor("zombie", "groan")
            }
        }
    }
}

fun progressPath(enemyComponent: AgentProperties, currentPosition: Vector2): Boolean {
    if (enemyComponent.needsNewNextPosition && !enemyComponent.path.isEmpty) {
        enemyComponent.nextPosition = enemyComponent.path.removeFirst()
        enemyComponent.needsNewNextPosition = false
        stateBooleanFact(false, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
        inject<TurboFactsOfTheWorld>().setBooleanFact(false, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
    }
    if (currentPosition.dst(enemyComponent.nextPosition) <= 1f) {
        enemyComponent.nextPosition = vec2()
        enemyComponent.needsNewNextPosition = true
        stateBooleanFact(true, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
        inject<TurboFactsOfTheWorld>().setBooleanFact(true, "Enemy", enemyComponent.id.toString(), "ReachedWayPoint")
    }

    val direction = enemyComponent.nextPosition.cpy().sub(currentPosition).nor()
    enemyComponent.directionVector.set(direction)
    return enemyComponent.path.isEmpty
}

fun findPathFromTo(q: Queue<Vector2>, from: Coordinate, to: Coordinate) {
    q.clear()
    val mapManager = inject<GridMapManager>()
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
    val mapManager = inject<GridMapManager>()
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

