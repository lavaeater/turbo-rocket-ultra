package twodee.music

import com.badlogic.gdx.audio.Sound

class SamplersManager {
    val samplers = mutableMapOf<SampleFile, SimpleSampler>()
}

interface SoundScheduler {
    fun addSound(sound: Sound, pitch: Float, scheduledTime: Float)
}

class SoundsToPlayScheduler: SoundScheduler {
    val toPlay = mutableListOf<SimplePlayableNote>()
    override fun addSound(sound: Sound, pitch: Float, scheduledTime: Float) {
        toPlay.add(SimplePlayableNote(sound, pitch, scheduledTime))
    }
}

class SimplePlayableNote(val sound: Sound, pitch: Float, scheduledTime: Float)

class SimpleSampler(val name: String, private val sound: Sound, private val soundScheduler: SoundScheduler) {
    fun play(midiNoteDiff: Int, scheduledTime: Float) {
        soundScheduler.addSound(sound, midiNoteDiff.toPitch(), scheduledTime)
    }

    override fun toString(): String {
        return name
    }
}

class Sampler(private val soundSource: Sound) {
    fun play(midiNoteDiff: Int, scheduledTime: Float) {
        ToPlay.soundsToPlay.add(PlayableNote(soundSource, midiNoteDiff.toPitch(), scheduledTime))
    }
}


