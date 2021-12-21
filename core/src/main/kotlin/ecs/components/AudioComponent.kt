package ecs.components

import com.badlogic.gdx.audio.Sound
import ecs.components.ai.CoolDownComponent
import ecs.systems.ai.EmptySound

class AudioComponent: CoolDownComponent() {
    var soundEffect: Sound = EmptySound()
    override fun reset() {
        soundEffect = EmptySound()
        super.reset()
    }
}