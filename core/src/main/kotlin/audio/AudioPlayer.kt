package audio

import tru.Assets

class AudioPlayer(private val defaultVolume : Float = 0.2f) {

    fun playSound(sound: String, volume: Float = defaultVolume) {
        Assets.soundEffects[sound]?.play(volume)
    }

    private val queuedSounds = mutableListOf<QueuedSound>()
    private val soundsToPlay = mutableListOf<QueuedSound>()

    fun playSounds(sounds: Map<String, Float>) {
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

data class QueuedSound(val sound: String, val delay: Float, var delta:Float = 0f)