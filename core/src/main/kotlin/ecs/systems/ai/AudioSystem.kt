package ecs.systems.ai

import audio.AudioPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.AudioComponent
import ecs.components.gameplay.TransformComponent
import injection.Context
import ktx.ashley.allOf
import ktx.math.random
import physics.audio

class AudioSystem : IteratingSystem(allOf(AudioComponent::class, TransformComponent::class).get()) {
    private val audioPlayer by lazy { Context.inject<AudioPlayer>() }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val audioComponent = entity.audio()
        audioComponent.coolDown -= deltaTime
        if(audioComponent.coolDown < 0f) {
            audioComponent.coolDown = audioComponent.coolDownRange.random()
            audioPlayer.playSound(audioComponent.soundEffect, 1f)
        }
    }
}