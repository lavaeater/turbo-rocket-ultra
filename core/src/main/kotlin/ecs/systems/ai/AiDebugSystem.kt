package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ecs.components.ai.BehaviorComponent
import ecs.components.gameplay.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import physics.behavior
import physics.sprite
import tru.Assets

class AiDebugSystem : IteratingSystem(allOf(BehaviorComponent::class, TransformComponent::class).get()) {
    val batch by lazy { inject<PolygonSpriteBatch>() }

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val behaviorComponent = entity.behavior()
        val spriteComponent = entity.sprite()
        if(Assets.aiDebugBadges.containsKey(behaviorComponent.toString()))
            spriteComponent.extraSprites["aidebug"] = Assets.aiDebugBadges[behaviorComponent.toString()]!!
    }
}