package ecs.systems.ai

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.GdxAI

class UpdateTimePieceSystem : EntitySystem() {
    override fun update(deltaTime: Float) {
        GdxAI.getTimepiece().update(deltaTime)
    }
}