package ecs.systems.ai

import ai.pathfinding.TileGraph
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.Amble
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.systems.sectionX
import ecs.systems.sectionY
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.random
import ktx.math.vec2
import map.grid.Coordinate
import map.grid.GridMapGenerator
import map.grid.GridMapManager
import map.snake.randomPoint
import physics.getComponent

class AmblingSystem : IteratingSystem(allOf(Amble::class, EnemyComponent::class, TransformComponent::class).get()) {

    private val mapManager by lazy { inject<GridMapManager>() }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<Amble>()
        val currentPosition = entity.getComponent<TransformComponent>().position
        if(component.firstRun) {

            val currentSection = TileGraph.createCoordinate(currentPosition.sectionX(), currentPosition.sectionY())
            //1. Randomly select a section to move to
            val randomSection = mapManager.getRandomSection(currentSection)
            //2. Pathfind a path to it

            val path = mapManager.sectionGraph.findPath(currentSection, TileGraph.createCoordinate(randomSection.x, randomSection.y))
            for (i in 0 until path.count) {
                val coord = path.get(i)
                val section = mapManager.gridMap[coord]!!
                val someSpotInThatPlace = section.innerBounds.randomPoint()
                component.path.addLast(someSpotInThatPlace)
            }
            component.firstRun = false
        }
        if(component.needsNew) {
            component.nextPosition = component.path.removeFirst()
            component.needsNew = false
        }
        if(currentPosition.dst(component.nextPosition) <= 2f) {
            component.nextPosition = vec2()
            component.needsNew = true
        }

        val direction = component.nextPosition.cpy().sub(currentPosition).nor()
        entity.getComponent<EnemyComponent>().directionVector.set(direction)

        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f)
                component.status = Task.Status.SUCCEEDED
            if(component.path.isEmpty)
                component.status = Task.Status.SUCCEEDED
        }
    }
}

class OldAmblingSystem : IteratingSystem(allOf(Amble::class, EnemyComponent::class).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<Amble>()
        if(component.firstRun) {



            component.firstRun = false
            val directionRange = -1f..1f
            entity.getComponent<EnemyComponent>().directionVector.set(directionRange.random(), directionRange.random()).nor()
        }
        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f)
                component.status = Task.Status.SUCCEEDED
        }
    }
}
