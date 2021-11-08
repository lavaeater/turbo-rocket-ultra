package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ecs.components.ai.BehaviorComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.TextureComponent
import ecs.components.graphics.renderables.AnimatedCharacterComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.vec2
import physics.getComponent
import tru.Assets
import ui.simple.BoundTextActor
import ui.simple.SimpleContainer

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
            textureComponent.extraTextures["aidebug"] = Assets.aiDebugBadges[behaviorComponent.toString()]!!
//
//        textToPrint = behaviorComponent.toString()
//        Assets.debugFont.draw(
//            batch,
//            textToPrint,
//            position.x,
//            position.y
//        )
    }
}