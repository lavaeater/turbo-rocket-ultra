package ecs.components

import audio.TurboSound
import ecs.components.ai.CoolDownComponent

class AudioComponent: CoolDownComponent() {
    var takeDistanceIntoAccount = true
    var channel = AudioChannels.simultaneous
    var soundEffect: TurboSound? = null

    override fun reset() {
        takeDistanceIntoAccount = true
        soundEffect = null
        super.reset()
    }
}