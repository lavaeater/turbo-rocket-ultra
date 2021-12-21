package ecs.systems.ai

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ecs.components.AudioComponent
import ecs.components.gameplay.TransformComponent
import injection.Context.inject
import ktx.ashley.allOf
import ktx.math.random
import physics.audio
import physics.transform
import wastelandui.toVec2

class AudioSystem : IteratingSystem(allOf(AudioComponent::class, TransformComponent::class).get()) {
    private val audioPlayer by lazy { inject<AudioPlayer>() }
    private val camera by lazy { inject<OrthographicCamera>() }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val audioComponent = entity.audio()
        audioComponent.coolDown -= deltaTime
        if(audioComponent.coolDown < 0f) {
            var factor = 1f
            audioComponent.coolDown = audioComponent.coolDownRange.random()
            if(audioComponent.takeDistanceIntoAccount) {
                val soundPosition = entity.transform().position
                val distance = camera.position.toVec2().dst(soundPosition)
                if(distance > 5f)
                    factor = 1 / distance * 2
            }
            audioPlayer.playSound(audioComponent.soundEffect, 1f * factor)
        }
    }
}