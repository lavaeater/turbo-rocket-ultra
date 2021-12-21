package audio

import com.badlogic.gdx.audio.Sound
import tru.Assets

class AudioPlayer(private val defaultVolume : Float = 1f) {

    fun playSound(sound: Sound, volume: Float = defaultVolume) {
        sound.play(volume)
    }

    fun playSound(sound: String, volume: Float = defaultVolume) {
        Assets.soundEffects[sound]?.play(volume)
    }

    private val queuedSounds = mutableListOf<QueuedSound>()
    private val soundsToPlay = mutableListOf<QueuedSound>()

    fun playSounds(sounds: Map<Sound, Float>) {
        for (pair in sounds) {
            queuedSounds.add(QueuedSound(pair.key, pair.value))
        }
    }
    fun update(delta:Float) {
        for (sound in queuedSounds) {
            sound.delta += delta
            if(sound.delta > sound.delay)
                soundsToPlay.add(sound)
        }
        for(sound in soundsToPlay) {
            playSound(sound.sound)
            queuedSounds.remove(sound)
        }
        soundsToPlay.clear()
    }
}

