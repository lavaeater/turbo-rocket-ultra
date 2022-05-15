package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import ecs.components.ai.old.Amble
import ecs.components.enemy.AgentProperties
import ktx.ashley.allOf
import ktx.math.random
import physics.getComponent

class OldAmblingSystem : IteratingSystem(allOf(Amble::class, AgentProperties::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = entity.getComponent<Amble>()
        if(component.firstRun) {



            component.firstRun = false
            val directionRange = -1f..1f
            entity.getComponent<AgentProperties>().directionVector.set(directionRange.random(), directionRange.random()).nor()
        }
        if (component.status == Task.Status.RUNNING) {
            component.coolDown -= deltaTime
            if (component.coolDown <= 0f)
                component.status = Task.Status.SUCCEEDED
        }
    }
}