package ecs.systems.facts

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.PerimeterObjectiveComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerComponent
import ktx.ashley.allOf
import physics.objective
import physics.perimeter
import physics.transform

class PerimeterObjectiveSystem: IteratingSystem(allOf(PerimeterObjectiveComponent::class, TransformComponent::class).get())  {
    private val playerEntities get() = engine.getEntitiesFor(allOf(PlayerComponent::class, TransformComponent::class).get())
    private val playerTransforms get() = playerEntities.map { it.transform() }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        val perimeter = entity.perimeter()
        val objective = entity.objective()
        if(perimeter.hasEntered) {
            perimeter.timeLeft -= deltaTime
        } else {
            perimeter.timeLeft = perimeter.timeRequired
        }
        objective.touched = perimeter.timeLeft <= 0f
        perimeter.hasEntered = playerTransforms.any { it.position.dst(transform.position) < perimeter.distance }
    }
}