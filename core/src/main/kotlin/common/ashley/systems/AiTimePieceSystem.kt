package common.ashley.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.GdxAI

class AiTimePieceSystem: EntitySystem() {
    override fun update(deltaTime: Float) {
        GdxAI.getTimepiece().update(deltaTime)
    }
}