package ecs.systems.ai

import ai.pathfinding.TileGraph
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.Investigate
import ecs.components.ai.NoticedSomething
import ecs.components.enemy.AgentProperties
import ecs.components.gameplay.TransformComponent
import ecs.systems.sectionX
import ecs.systems.sectionY
import injection.Context.inject
import ktx.ashley.allOf
import map.grid.GridMapManager
import physics.getComponent

class InvestigateSystem : IteratingSystem(allOf(Investigate::class, AgentProperties::class, NoticedSomething::class).get()) {

    private val mapManager by lazy { inject<GridMapManager>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<Investigate>()
        val agentProperties = entity.getComponent<AgentProperties>()
        val currentPosition = entity.getComponent<TransformComponent>().position

        if(component.firstRun) {
            val notice = entity.getComponent<NoticedSomething>()
            val currentSection = TileGraph.getCoordinateInstance(currentPosition.sectionX(), currentPosition.sectionY())
            val noticeSection = TileGraph.getCoordinateInstance(notice.noticedWhere.sectionX(), notice.noticedWhere.sectionY())

            findPathFromTo(agentProperties, currentSection, noticeSection)
            component.firstRun = false
        }

        agentProperties.speed = 5f

        val weAreDone = progressPath(agentProperties, currentPosition)

        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f)
                component.status = Task.Status.FAILED
            if(weAreDone)
                component.status = Task.Status.FAILED
        }
    }
}