package ecs.systems.facts

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.gameplay.PerimeterObjectiveComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerComponent
import injection.Context.inject
import ktx.ashley.allOf
import physics.light
import physics.objective
import physics.perimeter
import physics.transform
import messaging.Message
import messaging.MessageHandler

class PerimeterObjectiveSystem: IteratingSystem(allOf(PerimeterObjectiveComponent::class, TransformComponent::class).get())  {
    private val playerEntities get() = engine.getEntitiesFor(allOf(PlayerComponent::class, TransformComponent::class).get())
    private val playerTransforms get() = playerEntities.map { it.transform() }
    private val messageHandler by lazy { inject<MessageHandler>() }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        val perimeter = entity.perimeter()
        val objective = entity.objective()
        val light = entity.light()
        if(perimeter.hasEntered) {
            perimeter.timeLeft -= deltaTime
            if(perimeter.firstEntry) {
                perimeter.firstEntry = false
                messageHandler.sendMessage(Message.ShowProgressBar(perimeter.timeRequired, transform.position) {
                    return@ShowProgressBar if(perimeter.firstEntry) perimeter.timeRequired else perimeter.timeLeft
                })
            }
        } else if(!objective.touched) {
            perimeter.timeLeft = perimeter.timeRequired
            if(!perimeter.firstEntry)
                perimeter.firstEntry = true
        }
        objective.touched = perimeter.timeLeft <= 0f
        light.light.isActive = objective.touched || perimeter.hasEntered
        perimeter.hasEntered = playerTransforms.any { it.position.dst(transform.position) < perimeter.distance }
    }
}