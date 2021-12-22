package ecs.components

import com.badlogic.gdx.audio.Sound
import ecs.components.ai.CoolDownComponent
import ecs.systems.ai.EmptySound

class AudioComponent: CoolDownComponent() {
    var takeDistanceIntoAccount = true
    var soundEffect: Sound = EmptySound()
    override fun reset() {
        takeDistanceIntoAccount = true
        soundEffect = EmptySound()
        super.reset()
    }
}