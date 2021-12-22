package ecs.systems.ai

import ai.pathfinding.TileGraph
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import ecs.components.ai.Amble
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.systems.sectionX
import ecs.systems.sectionY
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.vec2
import map.grid.Coordinate
import map.grid.GridMapManager
import map.snake.randomPoint
import physics.AshleyMappers
import physics.audio
import physics.hasAudio
import tru.Assets


class AmblingSystem : IteratingSystem(allOf(Amble::class, EnemyComponent::class, TransformComponent::class).get()) {

    private val mapManager by lazy { inject<GridMapManager>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = AshleyMappers.amble.get(entity)
        val enemyComponent = AshleyMappers.enemy.get(entity)
        val currentPosition = AshleyMappers.transform.get(entity).position

        if(component.firstRun) {

            val currentSection = TileGraph.getCoordinateInstance(currentPosition.sectionX(), currentPosition.sectionY())
            //1. Randomly select a section to move to
            val maxDistance = 10
            val randomSection = mapManager.getRandomSection(currentSection, maxDistance)
            //2. Pathfind a path to it
            findPathFromTo(enemyComponent, currentSection, randomSection)

            component.firstRun = false
            if(entity.hasAudio()) {
                val audio = entity.audio()
                audio.soundEffect = Assets.newSoundEffects["zombies"]!!["groans"]!!.random()
                audio.coolDownRange = 60f..120f
            }
        }

        val weAreDone = progressPath(enemyComponent, currentPosition)

        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f)
                component.status = Task.Status.SUCCEEDED
            if(weAreDone)
                component.status = Task.Status.SUCCEEDED
        }
    }
}

fun progressPath(enemyComponent: EnemyComponent, currentPosition: Vector2) : Boolean {
    if(enemyComponent.needsNewNextPosition && !enemyComponent.path.isEmpty) {
        enemyComponent.nextPosition = enemyComponent.path.removeFirst()
        enemyComponent.needsNewNextPosition = false
    }
    if(currentPosition.dst(enemyComponent.nextPosition) <= 1f) {
        enemyComponent.nextPosition = vec2()
        enemyComponent.needsNewNextPosition = true
    }

    val direction = enemyComponent.nextPosition.cpy().sub(currentPosition).nor()
    enemyComponent.directionVector.set(direction)
    return enemyComponent.path.isEmpty
}

fun findPathFromTo(enemyComponent: EnemyComponent, from: Coordinate, to:Coordinate) {
    val mapManager = inject<GridMapManager>()
    val path = mapManager.sectionGraph.findPath(from, to)
    for (i in 0 until path.count) {
        val target = path.get(i)
        val section = mapManager.gridMap[target]!!
        val someSpotInThatPlace = section.innerBounds.randomPoint()
        enemyComponent.path.addLast(someSpotInThatPlace)
    }
}

