package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ecs.components.ai.BehaviorComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.TextureComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import physics.getComponent
import tru.Assets

class AiDebugSystem : IteratingSystem(allOf(BehaviorComponent::class, TransformComponent::class).get()) {
    val batch by lazy { inject<PolygonSpriteBatch>() }
    var textToPrint = ""

    override fun update(deltaTime: Float) {
        batch.use {
            super.update(deltaTime)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val position = entity.getComponent<TransformComponent>().position
        val behaviorComponent = entity.getComponent<BehaviorComponent>()
        val textureComponent = entity.getComponent<TextureComponent>()
        if(Assets.aiDebugBadges.containsKey(behaviorComponent.toString()))
            textureComponent.extraTextures["aidebug"] = Pair(Assets.aiDebugBadges[behaviorComponent.toString()]!!, 1f)
    }
}