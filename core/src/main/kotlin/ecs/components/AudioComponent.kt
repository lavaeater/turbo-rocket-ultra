package ecs.components

import audio.TurboSound
import ecs.components.ai.CoolDownComponent

class AudioComponent: CoolDownComponent() {
    var takeDistanceIntoAccount = true
    var channel = AudioChannels.simultaneous
    var soundEffect: TurboSound? = null
    var playSound = false
    var playOnce = true
    fun playSound(sound: TurboSound, once: Boolean = true) {
        soundEffect = sound
        playSound = true
        playOnce = once
    }

    override fun reset() {
        takeDistanceIntoAccount = true
        soundEffect = null
        playOnce = true
        playSound = false
        super.reset()
    }

    override fun toString(): String {
        return "AudioComponent"
    }
}